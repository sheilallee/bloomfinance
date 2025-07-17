package br.edu.ifpb.pweb2.bloomfinance.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;

@Service
public class CorrentistaService {

    @Autowired
    private CorrentistaRepository correntistaRepository;

    public List<Correntista> findAll() {
        return correntistaRepository.findAll();
    }

    public Optional<Correntista> findById(Long id) {
        return correntistaRepository.findById(id);
    }

    public Correntista save(Correntista correntista) {
        return correntistaRepository.save(correntista);
    }

    public void deleteById(Long id) {
        correntistaRepository.deleteById(id);
    }

    public Optional<Correntista> findByEmail(String email) {
        return correntistaRepository.findByEmail(email);
    }
    
    public Page<Correntista> listarPaginadoOrdenadoPorIdDesc(Pageable pageable) {
        return correntistaRepository.findAll(pageable);
    }
}



