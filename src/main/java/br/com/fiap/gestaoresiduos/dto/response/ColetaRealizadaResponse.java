package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.ColetaRealizada;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ColetaRealizadaResponse(
        Long idColeta,
        Long idPonto,
        String enderecoPonto,
        LocalDate dataColeta,
        BigDecimal pesoRecolhidoKg,
        String empresaResponsavel
) {
    public static ColetaRealizadaResponse from(ColetaRealizada entity) {
        return new ColetaRealizadaResponse(
                entity.getIdColeta(),
                entity.getPontoColeta() != null ? entity.getPontoColeta().getIdPonto() : null,
                entity.getPontoColeta() != null ? entity.getPontoColeta().getEndereco() : null,
                entity.getDataColeta(),
                entity.getPesoRecolhidoKg(),
                entity.getEmpresaResponsavel()
        );
    }
}
