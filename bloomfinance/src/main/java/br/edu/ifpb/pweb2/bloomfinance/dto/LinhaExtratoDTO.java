package br.edu.ifpb.pweb2.bloomfinance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.edu.ifpb.pweb2.bloomfinance.model.Movimento;

public class LinhaExtratoDTO {
    private LocalDate data;
    private String categoria;
    private String descricao;
    private BigDecimal valor;
    private Movimento movimento;
    private Long idTransacao; 
    private BigDecimal saldoAcumulado;
    private boolean possuiComentario; 

    public LinhaExtratoDTO(LocalDate data, String categoria, String descricao,
                           BigDecimal valor, Movimento movimento,
                           Long idTransacao, BigDecimal saldoAcumulado,
                           boolean possuiComentario) { 
        this.data = data;
        this.categoria = categoria;
        this.descricao = descricao;
        this.valor = valor;
        this.movimento = movimento;
        this.idTransacao = idTransacao;
        this.saldoAcumulado = saldoAcumulado;
        this.possuiComentario = possuiComentario;
    }

    public LinhaExtratoDTO(LocalDate data, String categoria, String descricao,
                           BigDecimal valor, Movimento movimento,
                           Long idTransacao, BigDecimal saldoAcumulado) {
        this(data, categoria, descricao, valor, movimento, idTransacao, saldoAcumulado, false);
    }

    public LocalDate getData() { return data; }
    public String getCategoria() { return categoria; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public Movimento getMovimento() { return movimento; }
    public Long getIdTransacao() { return idTransacao; }
    public BigDecimal getSaldoAcumulado() { return saldoAcumulado; }

    public boolean isPossuiComentario() { return possuiComentario; } 
    public void setPossuiComentario(boolean possuiComentario) { this.possuiComentario = possuiComentario; }
}