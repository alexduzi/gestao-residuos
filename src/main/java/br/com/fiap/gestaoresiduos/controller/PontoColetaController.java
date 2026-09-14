package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.request.AtualizarVolumeRequest;
import br.com.fiap.gestaoresiduos.dto.request.PontoColetaRequest;
import br.com.fiap.gestaoresiduos.dto.response.PontoColetaResponse;
import br.com.fiap.gestaoresiduos.service.PontoColetaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "${api.prefix}/pontos-coleta")
public class PontoColetaController {

    private final PontoColetaService pontoColetaService;

    public PontoColetaController(PontoColetaService pontoColetaService) {
        this.pontoColetaService = pontoColetaService;
    }

    @GetMapping
    public ResponseEntity<List<PontoColetaResponse>> findAll() {
        return ResponseEntity.ok(pontoColetaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(pontoColetaService.findById(id));
    }

    @GetMapping("/tipo/{idTipo}")
    public ResponseEntity<List<PontoColetaResponse>> findByTipo(@PathVariable Long idTipo) {
        return ResponseEntity.ok(pontoColetaService.findByTipo(idTipo));
    }

    @PostMapping
    public ResponseEntity<PontoColetaResponse> create(@Valid @RequestBody PontoColetaRequest request) {
        PontoColetaResponse result = pontoColetaService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.idPonto())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PontoColetaResponse> update(@PathVariable Long id, @Valid @RequestBody PontoColetaRequest request) {
        return ResponseEntity.ok(pontoColetaService.update(id, request));
    }

    @PatchMapping("/{id}/volume")
    public ResponseEntity<PontoColetaResponse> updateVolume(@PathVariable Long id, @Valid @RequestBody AtualizarVolumeRequest request) {
        return ResponseEntity.ok(pontoColetaService.updateVolume(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        pontoColetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
