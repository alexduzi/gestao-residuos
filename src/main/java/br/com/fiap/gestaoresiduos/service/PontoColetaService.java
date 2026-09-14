package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import br.com.fiap.gestaoresiduos.dto.request.AtualizarVolumeRequest;
import br.com.fiap.gestaoresiduos.dto.request.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.dto.response.PontoColetaResponse;
import br.com.fiap.gestaoresiduos.exception.DatabaseException;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.AlertaCapacidadeRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import br.com.fiap.gestaoresiduos.repository.TipoResiduoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PontoColetaService {

    private static final BigDecimal LIMITE_ALERTA = new BigDecimal("0.90");
    private static final String MENSAGEM_ALERTA = "ALERTA ESG: O ponto de coleta atingiu ou excedeu 90% de sua capacidade.";

    private final PontoColetaRepository pontoColetaRepository;
    private final TipoResiduoRepository tipoResiduoRepository;
    private final AlertaCapacidadeRepository alertaCapacidadeRepository;

    public PontoColetaService(PontoColetaRepository pontoColetaRepository,
                              TipoResiduoRepository tipoResiduoRepository,
                              AlertaCapacidadeRepository alertaCapacidadeRepository) {
        this.pontoColetaRepository = pontoColetaRepository;
        this.tipoResiduoRepository = tipoResiduoRepository;
        this.alertaCapacidadeRepository = alertaCapacidadeRepository;
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponse> findAll() {
        return pontoColetaRepository.findAll().stream()
                .map(PontoColetaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PontoColetaResponse findById(Long id) {
        PontoColeta entity = buscar(id);
        return PontoColetaResponse.from(entity);
    }

    @Transactional(readOnly = true)
    public List<PontoColetaResponse> findByTipo(Long idTipo) {
        return pontoColetaRepository.findByTipoResiduo_IdTipo(idTipo).stream()
                .map(PontoColetaResponse::from)
                .toList();
    }

    @Transactional
    public PontoColetaResponse create(PontoColetaRequest dto) {
        TipoResiduo tipo = tipoResiduoRepository.findById(dto.idTipo())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de residuo nao encontrado: " + dto.idTipo()));

        PontoColeta entity = new PontoColeta();
        entity.setEndereco(dto.endereco());
        entity.setTipoResiduo(tipo);
        entity.setCapacidadeMaxKg(dto.capacidadeMaxKg());
        entity.setVolumeAtualKg(dto.volumeAtualKg());
        entity = pontoColetaRepository.save(entity);
        return PontoColetaResponse.from(entity);
    }

    @Transactional
    public PontoColetaResponse update(Long id, PontoColetaRequest dto) {
        PontoColeta entity = buscar(id);
        TipoResiduo tipo = tipoResiduoRepository.findById(dto.idTipo())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de residuo nao encontrado: " + dto.idTipo()));
        entity.setEndereco(dto.endereco());
        entity.setTipoResiduo(tipo);
        entity.setCapacidadeMaxKg(dto.capacidadeMaxKg());
        entity.setVolumeAtualKg(dto.volumeAtualKg());
        entity = pontoColetaRepository.save(entity);
        return PontoColetaResponse.from(entity);
    }

    @Transactional
    public PontoColetaResponse updateVolume(Long id, AtualizarVolumeRequest dto) {
        PontoColeta entity = buscar(id);
        entity.setVolumeAtualKg(dto.volumeAtualKg());
        entity = pontoColetaRepository.save(entity);
        gerarAlertaSeNecessario(entity);
        return PontoColetaResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!pontoColetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ponto de coleta nao encontrado: " + id);
        }
        try {
            pontoColetaRepository.deleteById(id);
        } catch (DataIntegrityViolationException ex) {
            throw new DatabaseException("Falha de integridade referencial");
        }
    }

    private PontoColeta buscar(Long id) {
        return pontoColetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ponto de coleta nao encontrado: " + id));
    }

    private void gerarAlertaSeNecessario(PontoColeta ponto) {
        BigDecimal capacidade = ponto.getCapacidadeMaxKg();
        BigDecimal volume = ponto.getVolumeAtualKg();
        if (capacidade == null || volume == null || capacidade.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }
        BigDecimal percentual = volume.divide(capacidade, 4, RoundingMode.HALF_UP);
        if (percentual.compareTo(LIMITE_ALERTA) >= 0) {
            AlertaCapacidade alerta = new AlertaCapacidade();
            alerta.setPontoColeta(ponto);
            alerta.setDataGeracao(LocalDate.now());
            alerta.setMensagem(MENSAGEM_ALERTA);
            alerta.setStatusResolvido("N");
            alertaCapacidadeRepository.save(alerta);
        }
    }
}
