package br.edu.ifpb.pweb2.bloomfinance.repository;

import br.edu.ifpb.pweb2.bloomfinance.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    List<Conta> findByCorrentistaId(Long correntistaId);
}


