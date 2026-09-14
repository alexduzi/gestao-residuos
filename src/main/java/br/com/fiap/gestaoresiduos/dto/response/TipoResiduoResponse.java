package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.TipoResiduo;

public record TipoResiduoResponse(
        Long idTipo,
        String descricao,
        String reciclavel,
        String perigoso
) {
    public static TipoResiduoResponse from(TipoResiduo entity) {
        return new TipoResiduoResponse(
                entity.getIdTipo(),
                entity.getDescricao(),
                entity.getReciclavel(),
                entity.getPerigoso()
        );
    }
}
