package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.request.TipoResiduoRequest;
import br.com.fiap.gestaoresiduos.dto.response.TipoResiduoResponse;
import br.com.fiap.gestaoresiduos.service.TipoResiduoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "${api.prefix}/tipos-residuo")
public class TipoResiduoController {

    private final TipoResiduoService tipoResiduoService;

    public TipoResiduoController(TipoResiduoService tipoResiduoService) {
        this.tipoResiduoService = tipoResiduoService;
    }

    @GetMapping
    public ResponseEntity<List<TipoResiduoResponse>> findAll() {
        return ResponseEntity.ok(tipoResiduoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TipoResiduoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(tipoResiduoService.findById(id));
    }

    @PostMapping
    public ResponseEntity<TipoResiduoResponse> create(@Valid @RequestBody TipoResiduoRequest request) {
        TipoResiduoResponse result = tipoResiduoService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.idTipo())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TipoResiduoResponse> update(@PathVariable Long id, @Valid @RequestBody TipoResiduoRequest request) {
        return ResponseEntity.ok(tipoResiduoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tipoResiduoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
