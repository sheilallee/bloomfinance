package br.edu.ifpb.pweb2.bloomfinance.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.ifpb.pweb2.bloomfinance.model.Conta;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import br.edu.ifpb.pweb2.bloomfinance.service.CategoriaService;
import br.edu.ifpb.pweb2.bloomfinance.service.ContaService;
import br.edu.ifpb.pweb2.bloomfinance.service.TransacaoService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(HttpSession session, Model model) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");
        List<Conta> contas = contaService.findByCorrentistaId(usuario.getId().longValue());
        model.addAttribute("contas", contas);
        model.addAttribute("titulo", "Transações");
        return "transacoes/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("transacao", new Transacao());
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("titulo", "Nova Transação");
        return "transacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Transacao transacao) {
        transacaoService.save(transacao);
        return "redirect:/transacoes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Transacao transacao = transacaoService.findById(id).orElseThrow();
        model.addAttribute("transacao", transacao);
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("titulo", "Editar Transação");
        return "transacoes/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        transacaoService.deleteById(id);
        return "redirect:/transacoes";
    }
}
