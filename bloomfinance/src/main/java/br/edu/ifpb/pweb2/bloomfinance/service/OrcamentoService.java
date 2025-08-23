package br.edu.ifpb.pweb2.bloomfinance.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.bloomfinance.dto.OrcamentoLinhaDTO;
import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import br.edu.ifpb.pweb2.bloomfinance.model.NaturezaCategoria;
import br.edu.ifpb.pweb2.bloomfinance.repository.TransacaoRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.projection.SomaMesCategoriaProjection;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrcamentoService {

    private final CategoriaService categoriaService;
    private final TransacaoRepository transacaoRepository;

    public Map<String, Object> montarOrcamento(Long correntistaId, int ano) {
        //Carrega todas as categorias ativas (para exibir linhas com 0,00 também)
        List<Categoria> categorias = categoriaService.findAtivas();

        // Cria linhas vazias (0,00) para cada categoria
        Map<Integer, OrcamentoLinhaDTO> linhasPorCategoria = new HashMap<>();
        for (Categoria c : categorias) {
            OrcamentoLinhaDTO linha = new OrcamentoLinhaDTO(
                    c.getId(), c.getNome(), c.getNatureza(), c.getOrdem()
            );
            linhasPorCategoria.put(c.getId(), linha);
        }

        // Busca totais agregados por categoria × mês no ano escolhido
        List<SomaMesCategoriaProjection> agregados =
                transacaoRepository.somatorioPorCategoriaEMes(correntistaId, ano);

        for (SomaMesCategoriaProjection p : agregados) {
            OrcamentoLinhaDTO linha = linhasPorCategoria.get(p.getCategoriaId());
            if (linha != null) {
                BigDecimal v = p.getTotal() != null ? p.getTotal() : BigDecimal.ZERO;
                linha.addValorNoMes(p.getMes(), v);
            }
        }

        // Separa por natureza e ordena por ordem 
        Comparator<OrcamentoLinhaDTO> comp = Comparator
                .comparing(OrcamentoLinhaDTO::getOrdem, Comparator.nullsLast(Integer::compareTo))
                .thenComparing(OrcamentoLinhaDTO::getCategoriaNome, String.CASE_INSENSITIVE_ORDER);

        List<OrcamentoLinhaDTO> entradas = linhasPorCategoria.values().stream()
                .filter(l -> l.getNatureza() == NaturezaCategoria.ENTRADA)
                .sorted(comp).collect(Collectors.toList());

        List<OrcamentoLinhaDTO> saidas = linhasPorCategoria.values().stream()
                .filter(l -> l.getNatureza() == NaturezaCategoria.SAIDA)
                .sorted(comp).collect(Collectors.toList());

        List<OrcamentoLinhaDTO> investimentos = linhasPorCategoria.values().stream()
                .filter(l -> l.getNatureza() == NaturezaCategoria.INVESTIMENTO)
                .sorted(comp).collect(Collectors.toList());

        // Anos disponíveis para o select 
        List<Integer> anos = transacaoRepository.anosDisponiveis(correntistaId);
        if (anos == null || anos.isEmpty()) {
            anos = List.of(LocalDate.now().getYear());
        }

        Map<String, Object> modelo = new HashMap<>();
        modelo.put("linhasEntrada", entradas);
        modelo.put("linhasSaida", saidas);
        modelo.put("linhasInvestimento", investimentos);
        modelo.put("anos", anos);
        modelo.put("anoSelecionado", ano);
        return modelo;
    }
}
