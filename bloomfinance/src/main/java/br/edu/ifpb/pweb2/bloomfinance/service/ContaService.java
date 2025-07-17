package br.edu.ifpb.pweb2.bloomfinance.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.edu.ifpb.pweb2.bloomfinance.model.Conta;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.TipoConta;
import br.edu.ifpb.pweb2.bloomfinance.repository.ContaRepository;

@Service
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CorrentistaService correntistaService;

    //para paginação
    public Page<Conta> findByCorrentistaId(Long correntistaId, Pageable pageable) {
    return contaRepository.findByCorrentistaId(correntistaId, pageable);
    }

    //para casos sem paginação (ex: TransacaoController)
    public List<Conta> findByCorrentistaId(Long correntistaId) {
    return contaRepository.findByCorrentistaId(correntistaId, Pageable.unpaged()).getContent();
    }

    public List<Conta> findAll() {
        return contaRepository.findAll();
    }

    public Optional<Conta> findById(Long id) {
        return contaRepository.findById(id);
    }

    // public Conta save(Conta conta) {
    //     return contaRepository.save(conta);
        //}
    
    public Conta save(Conta conta) {
        Correntista correntista = correntistaService
            .findById(conta.getCorrentista().getId())
            .orElseThrow(() -> new IllegalArgumentException("Correntista não encontrado"));

        conta.setCorrentista(correntista);

        // Regra: se for CARTAO, diaFechamento é obrigatório
        if (conta.getTipo() == TipoConta.CARTAO && conta.getDiaFechamento() == null) {
            throw new IllegalArgumentException("O dia de fechamento é obrigatório para cartões de crédito.");
        }

        return contaRepository.save(conta);
    }

    public void deleteById(Long id) {
        contaRepository.deleteById(id);
    }
}






