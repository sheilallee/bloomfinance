package br.edu.ifpb.pweb2.bloomfinance.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.ifpb.pweb2.bloomfinance.model.NaturezaCategoria;

public class OrcamentoLinhaDTO {

    private final Integer categoriaId;
    private final String categoriaNome;
    private final NaturezaCategoria natureza;
    private final Integer ordem;

    private final List<BigDecimal> meses = new ArrayList<>(Collections.nCopies(12, BigDecimal.ZERO));

    public OrcamentoLinhaDTO(Integer categoriaId, String categoriaNome, NaturezaCategoria natureza, Integer ordem) {
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.natureza = natureza;
        this.ordem = ordem;
    }

    public Integer getCategoriaId() { return categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public NaturezaCategoria getNatureza() { return natureza; }
    public Integer getOrdem() { return ordem; }
    public List<BigDecimal> getMeses() { return meses; }

    public void addValorNoMes(int mes1a12, BigDecimal valor) {
        if (mes1a12 < 1 || mes1a12 > 12) return;
        BigDecimal atual = meses.get(mes1a12 - 1);
        meses.set(mes1a12 - 1, atual.add(valor != null ? valor : BigDecimal.ZERO));
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (BigDecimal v : meses) {
            if (v != null) total = total.add(v);
        }
        return total;
    }
}
