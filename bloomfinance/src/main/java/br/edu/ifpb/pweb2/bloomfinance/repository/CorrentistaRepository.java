package br.edu.ifpb.pweb2.bloomfinance.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;

public interface CorrentistaRepository extends JpaRepository<Correntista, Long> {
    Optional<Correntista> findByEmail(String email);
    Optional<Correntista> findByUser_Username(String username);
}
