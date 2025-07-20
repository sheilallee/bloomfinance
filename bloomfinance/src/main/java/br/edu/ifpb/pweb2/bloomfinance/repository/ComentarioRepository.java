package br.edu.ifpb.pweb2.bloomfinance.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifpb.pweb2.bloomfinance.model.Comentario;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByTransacaoId(Long transacaoId);
}
