package br.com.fiap.gestaoresiduos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import br.com.fiap.gestaoresiduos.dto.request.AtualizarVolumeRequest;
import br.com.fiap.gestaoresiduos.dto.request.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.exception.DatabaseException;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.AlertaCapacidadeRepository;
import br.com.fiap.gestaoresiduos.repository.PontoColetaRepository;
import br.com.fiap.gestaoresiduos.repository.TipoResiduoRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class PontoColetaServiceTest {

    @Mock
    private PontoColetaRepository pontoColetaRepository;
    @Mock
    private TipoResiduoRepository tipoResiduoRepository;
    @Mock
    private AlertaCapacidadeRepository alertaCapacidadeRepository;

    private PontoColetaService pontoColetaService;

    @BeforeEach
    void setUp() {
        pontoColetaService = new PontoColetaService(pontoColetaRepository, tipoResiduoRepository, alertaCapacidadeRepository);
    }

    private PontoColeta pontoComCapacidade(BigDecimal capacidade, BigDecimal volumeAtual) {
        PontoColeta ponto = new PontoColeta();
        ponto.setIdPonto(1L);
        ponto.setEndereco("Rua Teste, 123");
        ponto.setCapacidadeMaxKg(capacidade);
        ponto.setVolumeAtualKg(volumeAtual);
        return ponto;
    }

    @Test
    void updateVolumeGeraAlertaQuandoAtingeNoventaPorCentoDaCapacidade() {
        PontoColeta ponto = pontoComCapacidade(new BigDecimal("100"), BigDecimal.ZERO);
        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(pontoColetaRepository.save(any(PontoColeta.class))).thenAnswer(inv -> inv.getArgument(0));

        pontoColetaService.updateVolume(1L, new AtualizarVolumeRequest(new BigDecimal("90")));

        ArgumentCaptor<AlertaCapacidade> captor = ArgumentCaptor.forClass(AlertaCapacidade.class);
        verify(alertaCapacidadeRepository, times(1)).save(captor.capture());
        assertThat(captor.getValue().getStatusResolvido()).isEqualTo("N");
        assertThat(captor.getValue().getPontoColeta()).isEqualTo(ponto);
    }

    @Test
    void updateVolumeNaoGeraAlertaQuandoAbaixoDoLimiteDeNoventaPorCento() {
        PontoColeta ponto = pontoComCapacidade(new BigDecimal("100"), BigDecimal.ZERO);
        when(pontoColetaRepository.findById(1L)).thenReturn(Optional.of(ponto));
        when(pontoColetaRepository.save(any(PontoColeta.class))).thenAnswer(inv -> inv.getArgument(0));

        pontoColetaService.updateVolume(1L, new AtualizarVolumeRequest(new BigDecimal("50")));

        verify(alertaCapacidadeRepository, never()).save(any());
    }

    @Test
    void createLancaResourceNotFoundExceptionQuandoTipoResiduoNaoExiste() {
        PontoColetaRequest dto = new PontoColetaRequest("Rua X", 99L, new BigDecimal("100"), BigDecimal.ZERO);
        when(tipoResiduoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pontoColetaService.create(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createSalvaPontoComTipoResiduoEncontrado() {
        TipoResiduo tipo = new TipoResiduo();
        tipo.setIdTipo(5L);
        tipo.setDescricao("Papel");
        PontoColetaRequest dto = new PontoColetaRequest("Rua X", 5L, new BigDecimal("200"), new BigDecimal("10"));
        when(tipoResiduoRepository.findById(5L)).thenReturn(Optional.of(tipo));
        when(pontoColetaRepository.save(any(PontoColeta.class))).thenAnswer(inv -> inv.getArgument(0));

        var response = pontoColetaService.create(dto);

        assertThat(response.endereco()).isEqualTo("Rua X");
        assertThat(response.capacidadeMaxKg()).isEqualByComparingTo("200");
    }

    @Test
    void deleteLancaResourceNotFoundExceptionQuandoPontoNaoExiste() {
        when(pontoColetaRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> pontoColetaService.delete(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteLancaDatabaseExceptionQuandoHaViolacaoDeIntegridadeReferencial() {
        when(pontoColetaRepository.existsById(1L)).thenReturn(true);
        org.mockito.Mockito.doThrow(new DataIntegrityViolationException("fk violation"))
                .when(pontoColetaRepository).deleteById(1L);

        assertThatThrownBy(() -> pontoColetaService.delete(1L))
                .isInstanceOf(DatabaseException.class);
    }
}
