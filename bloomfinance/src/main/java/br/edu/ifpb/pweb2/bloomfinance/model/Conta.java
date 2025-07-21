package br.edu.ifpb.pweb2.bloomfinance.model;


import java.io.Serializable;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Conta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^\\d{4}-\\d{1}$", message = "O número da conta deve seguir o formato 9999-9.")
    private String numero;
    private String descricao;

    @Enumerated(EnumType.STRING)
    private TipoConta tipo;

    private Integer diaFechamento;

    @ManyToOne
    @JoinColumn(name = "correntista_id")
    private Correntista correntista;

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transacao> transacoes;

    public boolean isCartao() {
        return this.tipo == TipoConta.CARTAO;
    }
}