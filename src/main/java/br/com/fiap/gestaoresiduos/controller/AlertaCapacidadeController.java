package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.response.AlertaCapacidadeResponse;
import br.com.fiap.gestaoresiduos.service.AlertaCapacidadeService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.prefix}/alertas")
public class AlertaCapacidadeController {

    private final AlertaCapacidadeService alertaCapacidadeService;

    public AlertaCapacidadeController(AlertaCapacidadeService alertaCapacidadeService) {
        this.alertaCapacidadeService = alertaCapacidadeService;
    }

    @GetMapping
    public ResponseEntity<List<AlertaCapacidadeResponse>> findAll() {
        return ResponseEntity.ok(alertaCapacidadeService.findAll());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<AlertaCapacidadeResponse>> findPendentes() {
        return ResponseEntity.ok(alertaCapacidadeService.findPendentes());
    }

    @GetMapping("/ponto/{idPonto}")
    public ResponseEntity<List<AlertaCapacidadeResponse>> findByPonto(@PathVariable Long idPonto) {
        return ResponseEntity.ok(alertaCapacidadeService.findByPonto(idPonto));
    }

    @PatchMapping("/{id}/resolver")
    public ResponseEntity<AlertaCapacidadeResponse> resolver(@PathVariable Long id) {
        return ResponseEntity.ok(alertaCapacidadeService.resolver(id));
    }
}
