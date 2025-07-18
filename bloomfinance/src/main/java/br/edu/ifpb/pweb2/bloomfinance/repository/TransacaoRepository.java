package br.edu.ifpb.pweb2.bloomfinance.repository;

import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaId(Long contaId);

    List<Transacao> findByContaIdAndDataBetween(Long contaId, LocalDate inicio, LocalDate fim);

    @Query("SELECT t FROM Transacao t WHERE t.conta.correntista.id = :correntistaId")
    Page<Transacao> findByCorrentistaId(@Param("correntistaId") Long correntistaId, Pageable pageable);

}
