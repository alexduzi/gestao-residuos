package br.com.fiap.gestaoresiduos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import br.com.fiap.gestaoresiduos.domain.ConsolidadoReciclagem;
import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import br.com.fiap.gestaoresiduos.dto.request.ColetaRealizadaRequest;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.AlertaCapacidadeRepository;
import br.com.fiap.gestaoresiduos.repository.ColetaRealizadaRepository;
import br.com.fiap.gestaoresiduos.repository.ConsolidadoReciclagemRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ColetaRealizadaServiceTest {

    @Mock
    private ColetaRealizadaRepository coletaRepository;
    @Mock
    private PontoColetaRepository pontoColetaRepository;
    @Mock
    private ConsolidadoReciclagemRepository consolidadoRepository;
    @Mock
    private AlertaCapacidadeRepository alertaRepository;

    private ColetaRealizadaService coletaRealizadaService;

    @BeforeEach
    void setUp() {
        coletaRealizadaService = new ColetaRealizadaService(coletaRepository, pontoColetaRepository, consolidadoRepository, alertaRepository);
    }

    private PontoColeta pontoComTipo(BigDecimal volumeAtual) {
        TipoResiduo tipo = new TipoResiduo();
        tipo.setIdTipo(5L);
        tipo.setDescricao("Plastico");

        PontoColeta ponto = new PontoColeta();
        ponto.setIdPonto(1L);
        ponto.setEndereco("Rua Teste, 123");
        ponto.setTipoResiduo(tipo);
        ponto.setCapacidadeMaxKg(new BigDecimal("500"));
        ponto.setVolumeAtualKg(volumeAtual);
        return ponto;
    }

    private ColetaRealizadaRequest requestPara(Long idPonto, BigDecimal peso) {
        return new ColetaRealizadaRequest(idPonto, LocalDate.of(2026, 3, 10), peso, "EcoTransporte");
    }

    @Test
    void createLancaResourceNotFoundExceptionQuandoPontoNaoExiste() {
        when(pontoColetaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> coletaRealizadaService.create(requestPara(99L, BigDecimal.TEN)))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createZeraOVolumeAtualDoPontoDeColetaAposRegistrarColeta() {
        PontoColeta ponto = pontoComTipo(new BigDecimal("80"));
        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(5L, "03/2026")).thenReturn(Optional.empty());
        when(alertaRepository.findByPontoColeta_IdPonto(1L)).thenReturn(List.of());

        coletaRealizadaService.create(requestPara(1L, new BigDecimal("15")));

        ArgumentCaptor<PontoColeta> captor = ArgumentCaptor.forClass(PontoColeta.class);
        verify(pontoColetaRepository).save(captor.capture());
        assertThat(captor.getValue().getVolumeAtualKg()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void createCriaNovoConsolidadoQuandoNaoExisteRegistroParaOMes() {
        PontoColeta ponto = pontoComTipo(new BigDecimal("80"));
        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(5L, "03/2026")).thenReturn(Optional.empty());
        when(alertaRepository.findByPontoColeta_IdPonto(1L)).thenReturn(List.of());

        coletaRealizadaService.create(requestPara(1L, new BigDecimal("15")));

        ArgumentCaptor<ConsolidadoReciclagem> captor = ArgumentCaptor.forClass(ConsolidadoReciclagem.class);
        verify(consolidadoRepository).save(captor.capture());
        assertThat(captor.getValue().getMesAno()).isEqualTo("03/2026");
        assertThat(captor.getValue().getTotalRecicladoKg()).isEqualByComparingTo("15");
    }

    @Test
    void createSomaAoTotalJaExistenteDoConsolidadoDoMes() {
        PontoColeta ponto = pontoComTipo(new BigDecimal("80"));
        ConsolidadoReciclagem existente = new ConsolidadoReciclagem();
        existente.setIdConsolidado(7L);
        existente.setTipoResiduo(ponto.getTipoResiduo());
        existente.setMesAno("03/2026");
        existente.setTotalRecicladoKg(new BigDecimal("10"));

        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(5L, "03/2026")).thenReturn(Optional.of(existente));
        when(alertaRepository.findByPontoColeta_IdPonto(1L)).thenReturn(List.of());

        coletaRealizadaService.create(requestPara(1L, new BigDecimal("5")));

        ArgumentCaptor<ConsolidadoReciclagem> captor = ArgumentCaptor.forClass(ConsolidadoReciclagem.class);
        verify(consolidadoRepository).save(captor.capture());
        assertThat(captor.getValue().getTotalRecicladoKg()).isEqualByComparingTo("15");
    }

    @Test
    void createResolveAlertasPendentesDoPontoDeColeta() {
        PontoColeta ponto = pontoComTipo(new BigDecimal("80"));
        AlertaCapacidade alertaPendente = new AlertaCapacidade();
        alertaPendente.setIdAlerta(3L);
        alertaPendente.setPontoColeta(ponto);
        alertaPendente.setStatusResolvido("N");

        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(5L, "03/2026")).thenReturn(Optional.empty());
        when(alertaRepository.findByPontoColeta_IdPonto(1L)).thenReturn(List.of(alertaPendente));

        coletaRealizadaService.create(requestPara(1L, new BigDecimal("15")));

        ArgumentCaptor<AlertaCapacidade> captor = ArgumentCaptor.forClass(AlertaCapacidade.class);
        verify(alertaRepository).save(captor.capture());
        assertThat(captor.getValue().getStatusResolvido()).isEqualTo("S");
    }

    @Test
    void createNaoReenviaAlertaQueJaEstavaResolvido() {
        PontoColeta ponto = pontoComTipo(new BigDecimal("80"));
        AlertaCapacidade alertaJaResolvido = new AlertaCapacidade();
        alertaJaResolvido.setIdAlerta(4L);
        alertaJaResolvido.setPontoColeta(ponto);
        alertaJaResolvido.setStatusResolvido("S");

        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(coletaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(consolidadoRepository.findByTipoResiduo_IdTipoAndMesAno(5L, "03/2026")).thenReturn(Optional.empty());
        when(alertaRepository.findByPontoColeta_IdPonto(1L)).thenReturn(List.of(alertaJaResolvido));

        coletaRealizadaService.create(requestPara(1L, new BigDecimal("15")));

        verify(alertaRepository, never()).save(any());
    }
}
