package br.com.fiap.gestaoresiduos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ColetaRealizadaRequest(
        @NotNull
        Long idPonto,

        @NotNull
        LocalDate dataColeta,

        @NotNull
        @Positive
        BigDecimal pesoRecolhidoKg,

        @NotBlank
        String empresaResponsavel
) {}
