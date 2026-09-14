package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.response.ConsolidadoReciclagemResponse;
import br.com.fiap.gestaoresiduos.service.ConsolidadoReciclagemService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${api.prefix}/consolidado")
public class ConsolidadoReciclagemController {

    private final ConsolidadoReciclagemService consolidadoReciclagemService;

    public ConsolidadoReciclagemController(ConsolidadoReciclagemService consolidadoReciclagemService) {
        this.consolidadoReciclagemService = consolidadoReciclagemService;
    }

    @GetMapping
    public ResponseEntity<List<ConsolidadoReciclagemResponse>> findAll() {
        return ResponseEntity.ok(consolidadoReciclagemService.findAll());
    }

    @GetMapping("/mes/{mesAno}")
    public ResponseEntity<List<ConsolidadoReciclagemResponse>> findByMesAno(@PathVariable String mesAno) {
        return ResponseEntity.ok(consolidadoReciclagemService.findByMesAno(mesAno));
    }

    @GetMapping("/tipo/{idTipo}")
    public ResponseEntity<List<ConsolidadoReciclagemResponse>> findByTipo(@PathVariable Long idTipo) {
        return ResponseEntity.ok(consolidadoReciclagemService.findByTipo(idTipo));
    }
}
