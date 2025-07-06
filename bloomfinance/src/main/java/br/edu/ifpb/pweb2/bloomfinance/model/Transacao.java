package br.edu.ifpb.pweb2.bloomfinance.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private Movimento movimento;

    @Enumerated(EnumType.STRING)
    private NaturezaCategoria natureza;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date data;

    @NumberFormat(pattern = "#,##0.00")
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
