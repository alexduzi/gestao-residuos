package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.dto.response.ConsolidadoReciclagemResponse;
import br.com.fiap.gestaoresiduos.repository.ConsolidadoReciclagemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConsolidadoReciclagemService {

    private final ConsolidadoReciclagemRepository consolidadoRepository;

    public ConsolidadoReciclagemService(ConsolidadoReciclagemRepository consolidadoRepository) {
        this.consolidadoRepository = consolidadoRepository;
    }

    @Transactional(readOnly = true)
    public List<ConsolidadoReciclagemResponse> findAll() {
        return consolidadoRepository.findAll().stream()
                .map(ConsolidadoReciclagemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsolidadoReciclagemResponse> findByMesAno(String mesAno) {
        return consolidadoRepository.findByMesAno(mesAno).stream()
                .map(ConsolidadoReciclagemResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsolidadoReciclagemResponse> findByTipo(Long idTipo) {
        return consolidadoRepository.findByTipoResiduo_IdTipo(idTipo).stream()
                .map(ConsolidadoReciclagemResponse::from)
                .toList();
    }
}
