package br.com.fiap.gestaoresiduos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PONTO_COLETA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idPonto")
public class PontoColeta {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqPontoColeta")
    @SequenceGenerator(name = "seqPontoColeta", sequenceName = "SEQ_PONTO_COLETA", allocationSize = 1)
    @Column(name = "ID_PONTO")
    private Long idPonto;

    @Column(name = "ENDERECO", nullable = false, length = 100)
    private String endereco;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO")
    private TipoResiduo tipoResiduo;

    @Column(name = "CAPACIDADE_MAX_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal capacidadeMaxKg;

    @Column(name = "VOLUME_ATUAL_KG", precision = 10, scale = 2)
    private BigDecimal volumeAtualKg;
}
