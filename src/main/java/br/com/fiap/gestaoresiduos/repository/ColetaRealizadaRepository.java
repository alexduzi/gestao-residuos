package br.com.fiap.gestaoresiduos.repository;

import br.com.fiap.gestaoresiduos.domain.ColetaRealizada;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ColetaRealizadaRepository extends JpaRepository<ColetaRealizada, Long> {
    List<ColetaRealizada> findByPontoColeta_IdPonto(Long idPonto);
}
