package br.edu.ifpb.pweb2.bloomfinance.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.bloomfinance.model.Conta;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    Page<Conta> findByCorrentistaId(Long correntistaId, Pageable pageable);
    List<Conta> findByCorrentistaId(Long correntistaId);
}
