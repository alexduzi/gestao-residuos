package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.PontoColeta;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PontoColetaRepository extends JpaRepository<PontoColeta, Long> {
    List<PontoColeta> findByTipoResiduo_IdTipo(Long idTipo);
}
