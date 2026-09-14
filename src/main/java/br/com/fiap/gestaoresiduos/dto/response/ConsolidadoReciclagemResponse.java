package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.ConsolidadoReciclagem;
import java.math.BigDecimal;

public record ConsolidadoReciclagemResponse(
        Long idConsolidado,
        String tipoResiduo,
        String mesAno,
        BigDecimal totalRecicladoKg
) {
    public static ConsolidadoReciclagemResponse from(ConsolidadoReciclagem entity) {
        return new ConsolidadoReciclagemResponse(
                entity.getIdConsolidado(),
                entity.getTipoResiduo() != null ? entity.getTipoResiduo().getDescricao() : null,
                entity.getMesAno(),
                entity.getTotalRecicladoKg()
        );
    }
}
