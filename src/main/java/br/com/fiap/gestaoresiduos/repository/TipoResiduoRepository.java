package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {
}
