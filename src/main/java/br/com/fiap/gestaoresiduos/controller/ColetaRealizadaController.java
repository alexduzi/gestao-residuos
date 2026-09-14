package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.request.ColetaRealizadaRequest;
import br.com.fiap.gestaoresiduos.dto.response.ColetaRealizadaResponse;
import br.com.fiap.gestaoresiduos.service.ColetaRealizadaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "${api.prefix}/coletas")
public class ColetaRealizadaController {

    private final ColetaRealizadaService coletaRealizadaService;

    public ColetaRealizadaController(ColetaRealizadaService coletaRealizadaService) {
        this.coletaRealizadaService = coletaRealizadaService;
    }

    @GetMapping
    public ResponseEntity<List<ColetaRealizadaResponse>> findAll() {
        return ResponseEntity.ok(coletaRealizadaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ColetaRealizadaResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(coletaRealizadaService.findById(id));
    }

    @GetMapping("/ponto/{idPonto}")
    public ResponseEntity<List<ColetaRealizadaResponse>> findByPonto(@PathVariable Long idPonto) {
        return ResponseEntity.ok(coletaRealizadaService.findByPonto(idPonto));
    }

    @PostMapping
    public ResponseEntity<ColetaRealizadaResponse> create(@Valid @RequestBody ColetaRealizadaRequest request) {
        ColetaRealizadaResponse result = coletaRealizadaService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.idColeta())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }
}
