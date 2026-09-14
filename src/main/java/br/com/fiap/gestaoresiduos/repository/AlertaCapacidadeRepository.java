package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.AlertaCapacidade;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertaCapacidadeRepository extends JpaRepository<AlertaCapacidade, Long> {
    List<AlertaCapacidade> findByPontoColeta_IdPonto(Long idPonto);

    List<AlertaCapacidade> findByStatusResolvido(String statusResolvido);
}
