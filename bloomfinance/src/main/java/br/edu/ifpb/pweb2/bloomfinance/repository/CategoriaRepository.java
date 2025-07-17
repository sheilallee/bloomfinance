package br.edu.ifpb.pweb2.bloomfinance.repository;

import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByAtivaTrueOrderByNaturezaAscOrdemAsc();
    Page<Categoria> findByAtivaTrue(Pageable pageable);
}