package br.com.fiap.gestaoresiduos.controller;

import br.com.fiap.gestaoresiduos.dto.request.NotificacaoRequest;
import br.com.fiap.gestaoresiduos.dto.response.NotificacaoResponse;
import br.com.fiap.gestaoresiduos.service.NotificacaoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "${api.prefix}/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;

    public NotificacaoController(NotificacaoService notificacaoService) {
        this.notificacaoService = notificacaoService;
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<NotificacaoResponse>> findByUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(notificacaoService.findByUsuario(idUsuario));
    }

    @PatchMapping("/{id}/lida")
    public ResponseEntity<NotificacaoResponse> marcarLida(@PathVariable Long id) {
        return ResponseEntity.ok(notificacaoService.marcarLida(id));
    }

    @PostMapping
    public ResponseEntity<NotificacaoResponse> create(@Valid @RequestBody NotificacaoRequest request) {
        NotificacaoResponse result = notificacaoService.create(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(result.idNotificacao())
                .toUri();
        return ResponseEntity.created(location).body(result);
    }
}
