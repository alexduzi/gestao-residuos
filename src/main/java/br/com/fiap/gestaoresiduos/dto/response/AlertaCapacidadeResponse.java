package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import java.time.LocalDate;

public record AlertaCapacidadeResponse(
        Long idAlerta,
        Long idPonto,
        String enderecoPonto,
        LocalDate dataGeracao,
        String mensagem,
        String statusResolvido
) {
    public static AlertaCapacidadeResponse from(AlertaCapacidade entity) {
        return new AlertaCapacidadeResponse(
                entity.getIdAlerta(),
                entity.getPontoColeta() != null ? entity.getPontoColeta().getIdPonto() : null,
                entity.getPontoColeta() != null ? entity.getPontoColeta().getEndereco() : null,
                entity.getDataGeracao(),
                entity.getMensagem(),
                entity.getStatusResolvido()
        );
    }
}
