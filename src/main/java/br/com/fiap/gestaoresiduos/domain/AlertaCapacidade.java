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
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ALERTA_CAPACIDADE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idAlerta")
public class AlertaCapacidade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqAlertaCapacidade")
    @SequenceGenerator(name = "seqAlertaCapacidade", sequenceName = "SEQ_ALERTA_CAPACIDADE", allocationSize = 1)
    @Column(name = "ID_ALERTA")
    private Long idAlerta;

    @ManyToOne
    @JoinColumn(name = "ID_PONTO")
    private PontoColeta pontoColeta;

    @Column(name = "DATA_GERACAO")
    private LocalDate dataGeracao;

    @Column(name = "MENSAGEM", length = 200)
    private String mensagem;

    @Column(name = "STATUS_RESOLVIDO", length = 1)
    private String statusResolvido;
}
