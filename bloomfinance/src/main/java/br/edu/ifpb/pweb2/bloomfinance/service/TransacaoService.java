package br.edu.ifpb.pweb2.bloomfinance.service;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import br.edu.ifpb.pweb2.bloomfinance.repository.TransacaoRepository;

@Service
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    public List<Transacao> findByContaId(Long contaId) {
        return transacaoRepository.findByContaId(contaId);
    }

    public List<Transacao> findByContaIdAndDataBetween(Long contaId, LocalDate inicio, LocalDate fim) {
        return transacaoRepository.findByContaIdAndDataBetween(contaId, inicio, fim);
    }

    public Optional<Transacao> findById(Long id) {
        return transacaoRepository.findById(id);
    }

    public Transacao save(Transacao transacao) {
        return transacaoRepository.save(transacao);
    }

    public void deleteById(Long id) {
        transacaoRepository.deleteById(id);
    }
}
