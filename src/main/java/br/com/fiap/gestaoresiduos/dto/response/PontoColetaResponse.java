package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import java.math.BigDecimal;

public record PontoColetaResponse(
        Long idPonto,
        String endereco,
        TipoResiduoResponse tipo,
        BigDecimal capacidadeMaxKg,
        BigDecimal volumeAtualKg
) {
    public static PontoColetaResponse from(PontoColeta entity) {
        return new PontoColetaResponse(
                entity.getIdPonto(),
                entity.getEndereco(),
                entity.getTipoResiduo() != null ? TipoResiduoResponse.from(entity.getTipoResiduo()) : null,
                entity.getCapacidadeMaxKg(),
                entity.getVolumeAtualKg()
        );
    }
}
