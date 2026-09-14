package br.com.fiap.gestaoresiduos.dto.response;

import br.com.fiap.gestaoresiduos.domain.Notificacao;
import java.time.LocalDate;

public record NotificacaoResponse(
        Long idNotificacao,
        String nomeUsuario,
        String enderecoPonto,
        String mensagem,
        LocalDate dataEnvio,
        String lida
) {
    public static NotificacaoResponse from(Notificacao entity) {
        return new NotificacaoResponse(
                entity.getIdNotificacao(),
                entity.getUsuario() != null ? entity.getUsuario().getNome() : null,
                entity.getPontoColeta() != null ? entity.getPontoColeta().getEndereco() : null,
                entity.getMensagem(),
                entity.getDataEnvio(),
                entity.getLida()
        );
    }
}
