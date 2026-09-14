package br.com.fiap.gestaoresiduos.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record AtualizarVolumeRequest(
        @NotNull
        @PositiveOrZero
        BigDecimal volumeAtualKg
) {}
