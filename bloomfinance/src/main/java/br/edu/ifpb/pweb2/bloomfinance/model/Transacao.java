package br.edu.ifpb.pweb2.bloomfinance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.NumberFormat;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class Transacao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @NotNull(message = "O movimento é obrigatório.")
    @Enumerated(EnumType.STRING)
    private Movimento movimento;

    @NotNull(message = "A data é obrigatória.")
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date data;

    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero.")
    @NotNull(message = "O valor é obrigatório.")
    private BigDecimal valor;

    @NotNull(message = "A conta é obrigatória.")
    @ManyToOne
    @JoinColumn(name = "conta_id")
    private Conta conta;

    @NotNull(message = "A categoria é obrigatória.")
    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;
}
