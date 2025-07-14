package br.edu.ifpb.pweb2.bloomfinance.repository;

import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByAtivaTrueOrderByNaturezaAscOrdemAsc();
}