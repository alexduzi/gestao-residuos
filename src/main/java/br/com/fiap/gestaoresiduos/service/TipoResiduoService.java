package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import br.com.fiap.gestaoresiduos.dto.request.TipoResiduoRequest;
import br.com.fiap.gestaoresiduos.dto.response.TipoResiduoResponse;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.repository.TipoResiduoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoResiduoService {

    private final TipoResiduoRepository tipoResiduoRepository;

    public TipoResiduoService(TipoResiduoRepository tipoResiduoRepository) {
        this.tipoResiduoRepository = tipoResiduoRepository;
    }

    @Transactional(readOnly = true)
    public List<TipoResiduoResponse> findAll() {
        return tipoResiduoRepository.findAll().stream()
                .map(TipoResiduoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoResiduoResponse findById(Long id) {
        TipoResiduo entity = tipoResiduoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de residuo nao encontrado: " + id));
        return TipoResiduoResponse.from(entity);
    }

    @Transactional
    public TipoResiduoResponse create(TipoResiduoRequest dto) {
        TipoResiduo entity = new TipoResiduo();
        entity.setDescricao(dto.descricao());
        entity.setReciclavel(dto.reciclavel());
        entity.setPerigoso(dto.perigoso());
        entity = tipoResiduoRepository.save(entity);
        return TipoResiduoResponse.from(entity);
    }

    @Transactional
    public TipoResiduoResponse update(Long id, TipoResiduoRequest dto) {
        TipoResiduo entity = tipoResiduoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de residuo nao encontrado: " + id));
        entity.setDescricao(dto.descricao());
        entity.setReciclavel(dto.reciclavel());
        entity.setPerigoso(dto.perigoso());
        entity = tipoResiduoRepository.save(entity);
        return TipoResiduoResponse.from(entity);
    }

    @Transactional
    public void delete(Long id) {
        if (!tipoResiduoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de residuo nao encontrado: " + id);
        }
        tipoResiduoRepository.deleteById(id);
    }
}
