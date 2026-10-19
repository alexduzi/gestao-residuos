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
        // O mes e gravado como "MM/yyyy", mas a barra nao pode trafegar no path
        // (/consolidado/mes/10/2025 vira outra rota e %2F e rejeitado pelo Tomcat),
        // entao o endpoint recebe "MM-yyyy" e converte aqui.
        return consolidadoRepository.findByMesAno(mesAno.replace('-', '/')).stream()
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
