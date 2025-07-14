package br.edu.ifpb.pweb2.bloomfinance.repository;

import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaId(Long contaId);

    List<Transacao> findByContaIdAndDataBetween(Long contaId, LocalDate inicio, LocalDate fim);
}
