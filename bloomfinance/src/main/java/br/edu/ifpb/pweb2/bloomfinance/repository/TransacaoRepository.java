package br.edu.ifpb.pweb2.bloomfinance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import br.edu.ifpb.pweb2.bloomfinance.repository.projection.SomaMesCategoriaProjection;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByContaId(Long contaId);

    List<Transacao> findByContaIdAndDataBetween(Long contaId, LocalDate inicio, LocalDate fim);

    @Query("SELECT t FROM Transacao t WHERE t.conta.correntista.id = :correntistaId")
    Page<Transacao> findByCorrentistaId(@Param("correntistaId") Long correntistaId, Pageable pageable);

    @Query(value = """
        SELECT 
            cat.id                         AS categoriaId,
            cat.nome                       AS categoriaNome,
            cat.natureza                   AS natureza,
            EXTRACT(MONTH FROM t.data)::int AS mes,
            SUM(t.valor)                   AS total
        FROM public.transacao t
        JOIN public.conta c   ON c.id = t.conta_id
        JOIN public.categoria cat ON cat.id = t.categoria_id
        WHERE c.correntista_id = :correntistaId
          AND EXTRACT(YEAR FROM t.data) = :ano
        GROUP BY cat.id, cat.nome, cat.natureza, mes
        ORDER BY cat.natureza, cat.nome
        """, nativeQuery = true)
    List<SomaMesCategoriaProjection> somatorioPorCategoriaEMes(
            @Param("correntistaId") Long correntistaId,
            @Param("ano") int ano
    );

    @Query(value = """
        SELECT DISTINCT EXTRACT(YEAR FROM t.data)::int AS ano
        FROM public.transacao t
        JOIN public.conta c ON c.id = t.conta_id
        WHERE c.correntista_id = :correntistaId
        ORDER BY ano
        """, nativeQuery = true)
    List<Integer> anosDisponiveis(@Param("correntistaId") Long correntistaId);
}
