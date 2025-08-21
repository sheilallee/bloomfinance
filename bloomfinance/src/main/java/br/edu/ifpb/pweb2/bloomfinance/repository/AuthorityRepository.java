package br.edu.ifpb.pweb2.bloomfinance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.bloomfinance.model.Authority;
import br.edu.ifpb.pweb2.bloomfinance.model.User;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {
    List<Authority> findByUser(User user);
}
