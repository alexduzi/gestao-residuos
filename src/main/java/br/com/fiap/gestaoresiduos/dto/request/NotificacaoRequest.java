package br.com.fiap.gestaoresiduos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NotificacaoRequest(
        @NotNull
        Long idUsuario,

        @NotNull
        Long idPonto,

        @NotBlank
        @Size(max = 300)
        String mensagem
) {}
