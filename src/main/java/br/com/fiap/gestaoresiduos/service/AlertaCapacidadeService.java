package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import br.com.fiap.gestaoresiduos.dto.response.AlertaCapacidadeResponse;
import br.com.fiap.gestaoresiduos.exception.BusinessException;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.AlertaCapacidadeRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AlertaCapacidadeService {

    private final AlertaCapacidadeRepository alertaRepository;

    public AlertaCapacidadeService(AlertaCapacidadeRepository alertaRepository) {
        this.alertaRepository = alertaRepository;
    }

    @Transactional(readOnly = true)
    public List<AlertaCapacidadeResponse> findAll() {
        return alertaRepository.findAll().stream()
                .map(AlertaCapacidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaCapacidadeResponse> findPendentes() {
        return alertaRepository.findByStatusResolvido("N").stream()
                .map(AlertaCapacidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AlertaCapacidadeResponse> findByPonto(Long idPonto) {
        return alertaRepository.findByPontoColeta_IdPonto(idPonto).stream()
                .map(AlertaCapacidadeResponse::from)
                .toList();
    }

    @Transactional
    public AlertaCapacidadeResponse resolver(Long id) {
        AlertaCapacidade alerta = alertaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta nao encontrado: " + id));
        if ("S".equals(alerta.getStatusResolvido())) {
            throw new BusinessException("Alerta ja esta resolvido.");
        }
        alerta.setStatusResolvido("S");
        alerta = alertaRepository.save(alerta);
        return AlertaCapacidadeResponse.from(alerta);
    }
}
