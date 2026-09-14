package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.ConsolidadoReciclagem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsolidadoReciclagemRepository extends JpaRepository<ConsolidadoReciclagem, Long> {
    List<ConsolidadoReciclagem> findByTipoResiduo_IdTipo(Long idTipo);

    List<ConsolidadoReciclagem> findByMesAno(String mesAno);

    Optional<ConsolidadoReciclagem> findByTipoResiduo_IdTipoAndMesAno(Long idTipo, String mesAno);
}
