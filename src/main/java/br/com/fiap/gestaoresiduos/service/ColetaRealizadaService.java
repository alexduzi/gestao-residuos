package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import br.com.fiap.gestaoresiduos.domain.ColetaRealizada;
import br.com.fiap.gestaoresiduos.domain.ConsolidadoReciclagem;
import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import br.com.fiap.gestaoresiduos.dto.request.ColetaRealizadaRequest;
import br.com.fiap.gestaoresiduos.dto.response.ColetaRealizadaResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.AlertaCapacidadeRepository;
import br.com.fiap.gestaoresiduos.repository.ColetaRealizadaRepository;
import br.com.fiap.gestaoresiduos.repository.ConsolidadoReciclagemRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ColetaRealizadaService {

    private static final DateTimeFormatter MES_ANO_FORMAT = DateTimeFormatter.ofPattern("MM/yyyy");

    private final ColetaRealizadaRepository coletaRepository;
    private final PontoColetaRepository pontoColetaRepository;
    private final ConsolidadoReciclagemRepository consolidadoRepository;
    private final AlertaCapacidadeRepository alertaRepository;

    public ColetaRealizadaService(ColetaRealizadaRepository coletaRepository,
                                  PontoColetaRepository pontoColetaRepository,
                                  ConsolidadoReciclagemRepository consolidadoRepository,
                                  AlertaCapacidadeRepository alertaRepository) {
        this.coletaRepository = coletaRepository;
        this.pontoColetaRepository = pontoColetaRepository;
        this.consolidadoRepository = consolidadoRepository;
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<ColetaRealizadaResponse> findAll() {
        return coletaRepository.findAll().stream()
                .map(ColetaRealizadaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ColetaRealizadaResponse findById(Long id) {
        ColetaRealizada entity = coletaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coleta nao encontrada: " + id));
        return ColetaRealizadaResponse.from(entity);
    }

    @Transactional(readOnly = true)
    public List<ColetaRealizadaResponse> findByPonto(Long idPonto) {
        return coletaRepository.findByPontoColeta_IdPonto(idPonto).stream()
                .map(ColetaRealizadaResponse::from)
                .toList();
    }

    @Transactional
    public ColetaRealizadaResponse create(ColetaRealizadaRequest dto) {
        PontoColeta ponto = pontoColetaRepository.findById(dto.idPonto())
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta nao encontrado: " + dto.idPonto()));

        ColetaRealizada coleta = new ColetaRealizada();
        coleta.setPontoColeta(ponto);
        coleta.setDataColeta(dto.dataColeta());
        coleta.setPesoRecolhidoKg(dto.pesoRecolhidoKg());
        coleta.setEmpresaResponsavel(dto.empresaResponsavel());
        coleta = coletaRepository.save(coleta);

        ponto.setVolumeAtualKg(BigDecimal.ZERO);
        pontoColetaRepository.save(ponto);

        atualizarConsolidado(coleta);
        resolverAlertasPendentes(ponto);

        return ColetaRealizadaResponse.from(coleta);
    }

    private void atualizarConsolidado(ColetaRealizada coleta) {
        TipoResiduo tipo = coleta.getPontoColeta().getTipoResiduo();
        if (tipo == null) {
            return;
        }
        String mesAno = coleta.getDataColeta().format(MES_ANO_FORMAT);
        Optional<ConsolidadoReciclagem> existente =
                consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(tipo.getIdTipo(), mesAno);
        if (existente.isPresent()) {
            ConsolidadoReciclagem c = existente.get();
            BigDecimal total = c.getTotalRecicladoKg() == null ? BigDecimal.ZERO : c.getTotalRecicladoKg();
            c.setTotalRecicladoKg(total.add(coleta.getPesoRecolhidoKg()));
            consolidadoRepository.save(c);
        } else {
            ConsolidadoReciclagem novo = new ConsolidadoReciclagem();
            novo.setTipoResiduo(tipo);
            novo.setMesAno(mesAno);
            novo.setTotalRecicladoKg(coleta.getPesoRecolhidoKg());
            consolidadoRepository.save(novo);
        }
    }

    private void resolverAlertasPendentes(PontoColeta ponto) {
        List<AlertaCapacidade> pendentes = alertaRepository.findByPontoColeta_IdPonto(ponto.getIdPonto());
        for (AlertaCapacidade alerta : pendentes) {
            if ("N".equals(alerta.getStatusResolvido())) {
                alerta.setStatusResolvido("S");
                alertaRepository.save(alerta);
            }
        }
    }
}
