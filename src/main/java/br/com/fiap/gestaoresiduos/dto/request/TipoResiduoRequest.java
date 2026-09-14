package br.com.fiap.gestaoresiduos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TipoResiduoRequest(
        @NotBlank
        String descricao,

        @NotBlank
        @Pattern(regexp = "[SN]")
        String reciclavel,

        @NotBlank
        @Pattern(regexp = "[SN]")
        String perigoso
) {}
