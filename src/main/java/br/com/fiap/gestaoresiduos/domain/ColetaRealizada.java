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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "COLETA_REALIZADA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idColeta")
public class ColetaRealizada {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqColetaRealizada")
    @SequenceGenerator(name = "seqColetaRealizada", sequenceName = "SEQ_COLETA_REALIZADA", allocationSize = 1)
    @Column(name = "ID_COLETA")
    private Long idColeta;

    @ManyToOne
    @JoinColumn(name = "ID_PONTO")
    private PontoColeta pontoColeta;

    @Column(name = "DATA_COLETA")
    private LocalDate dataColeta;

    @Column(name = "PESO_RECOLHIDO_KG", nullable = false, precision = 10, scale = 2)
    private BigDecimal pesoRecolhidoKg;

    @Column(name = "EMPRESA_RESPONSAVEL", length = 50)
    private String empresaResponsavel;
}
