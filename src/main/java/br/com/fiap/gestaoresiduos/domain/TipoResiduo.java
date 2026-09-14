package br.com.fiap.gestaoresiduos.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TIPO_RESIDUO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idTipo")
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqTipoResiduo")
    @SequenceGenerator(name = "seqTipoResiduo", sequenceName = "SEQ_TIPO_RESIDUO", allocationSize = 1)
    @Column(name = "ID_TIPO")
    private Long idTipo;

    @Column(name = "DESCRICAO", nullable = false, length = 50)
    private String descricao;

    @Column(name = "RECICLAVEL", length = 1)
    private String reciclavel;

    @Column(name = "PERIGOSO", length = 1)
    private String perigoso;
}
