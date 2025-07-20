package br.edu.ifpb.pweb2.bloomfinance.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.bloomfinance.model.Comentario;
import br.edu.ifpb.pweb2.bloomfinance.repository.ComentarioRepository;

@Service
public class ComentarioService {

    @Autowired
    private ComentarioRepository comentarioRepository;

    public Comentario save(Comentario comentario) {
        return comentarioRepository.save(comentario);
    }

    public void deleteById(Long id) {
        comentarioRepository.deleteById(id);
    }

    public Optional<Comentario> findById(Long id) {
        return comentarioRepository.findById(id);
    }

    public List<Comentario> findByTransacaoId(Long transacaoId) {
        return comentarioRepository.findByTransacaoId(transacaoId);
    }
}
