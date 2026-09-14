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
@Table(name = "CONSOLIDADO_RECICLAGEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idConsolidado")
public class ConsolidadoReciclagem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqConsolidado")
    @SequenceGenerator(name = "seqConsolidado", sequenceName = "SEQ_CONSOLIDADO", allocationSize = 1)
    @Column(name = "ID_CONSOLIDADO")
    private Long idConsolidado;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO")
    private TipoResiduo tipoResiduo;

    @Column(name = "MES_ANO", length = 7)
    private String mesAno;

    @Column(name = "TOTAL_RECICLADO_KG", precision = 12, scale = 2)
    private BigDecimal totalRecicladoKg;
}
