package br.edu.ifpb.pweb2.bloomfinance.repository.projection;

import java.math.BigDecimal;

public interface SomaMesCategoriaProjection {
    Integer getCategoriaId();
    String getCategoriaNome();
    String getNatureza();    // (ENTRADA/SAIDA/INVESTIMENTO)
    Integer getMes();        
    BigDecimal getTotal();   // soma dos valores do mês/categoria
}
