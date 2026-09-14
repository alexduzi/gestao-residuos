package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.Notificacao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {
    List<Notificacao> findByUsuario_IdUsuario(Long idUsuario);
}
