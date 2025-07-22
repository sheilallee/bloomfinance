package br.edu.ifpb.pweb2.bloomfinance.controller;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public String listar(HttpSession session, Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");

       // if (usuario == null || usuario.isAdmin()) {
       if (usuario == null) {
            return "redirect:/auth";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Transacao> transacoesPage = transacaoService.findByCorrentista(usuario.getId(), pageable);

        model.addAttribute("transacoes", transacoesPage);
        model.addAttribute("titulo", "Minhas Transações");
        return "transacoes/list";
    }

    @GetMapping("/form")
    public String form(Model model,
                       @RequestParam(required = false) Long contaId,
                       HttpSession session) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");

        //if (usuario == null || usuario.isAdmin()) {
        if (usuario == null) {
            return "redirect:/auth";
        }

        Transacao transacao = new Transacao();

        if (contaId != null) {
            Conta conta = contaService.findById(contaId).orElseThrow();
            transacao.setConta(conta);
        }

        model.addAttribute("transacao", transacao);
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId())); 
        model.addAttribute("titulo", "Nova Transação");
        return "transacoes/form";
    }

/*    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Transacao transacao,
                        BindingResult result,
                        Model model,
                        HttpSession session,
                        @RequestParam("valor") String valorTexto) { 

        Correntista usuario = (Correntista) session.getAttribute("usuario");

        if (usuario == null || usuario.isAdmin()) {
            return "redirect:/auth";
        }

        //converte o valor manualmente usando o mesmo formatador
        try {
            NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
            Number number = format.parse(valorTexto.trim());
            transacao.setValor(BigDecimal.valueOf(number.doubleValue()));
        } catch (ParseException e) {
            result.rejectValue("valor", "valor.invalido", "Valor inválido");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAtivas());
            model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
            model.addAttribute("titulo", transacao.getId() == null ? "Nova Transação" : "Editar Transação");
            return "transacoes/form";
        }

        //preserva os comentários em edição
        if (transacao.getId() != null) {
            Transacao existente = transacaoService.findById(transacao.getId()).orElseThrow();
            transacao.setComentarios(existente.getComentarios());
        }

        transacaoService.save(transacao);
        return "redirect:/transacoes";
    } */
   
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Transacao transacao,  
                        BindingResult result,
                        Model model,
                        HttpSession session,
                        @RequestParam("valorTexto") String valorTexto) {

        Correntista usuario = (Correntista) session.getAttribute("usuario");
       // if (usuario == null || usuario.isAdmin()) {
        if (usuario == null) {
            return "redirect:/auth";
        }

        //verifica se a conta realmente pertence ao usuário logado
        if (transacao.getConta() == null || 
            !transacao.getConta().getCorrentista().getId().equals(usuario.getId())) {
            return "redirect:/transacoes";
        }

        //converte e valida o valor manualmente
        if (valorTexto != null && !valorTexto.isBlank()) {
            try {
                NumberFormat format = NumberFormat.getInstance(new Locale("pt", "BR"));
                Number number = format.parse(valorTexto.trim());
                BigDecimal valor = BigDecimal.valueOf(number.doubleValue());

                if (valor.compareTo(BigDecimal.ZERO) <= 0) {
                    result.rejectValue("valor", "valor.min", "O valor deve ser maior que zero.");
                } else {
                    transacao.setValor(valor);
                }

            } catch (ParseException e) {
                result.rejectValue("valor", "valor.invalido", "Valor inválido.");
            }
        } else {
            result.rejectValue("valor", "valor.obrigatorio", "O valor é obrigatório.");
        }

        //se houver erro, retorna para o formulário com os dados e mensagens
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAtivas());
            model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
            model.addAttribute("titulo", transacao.getId() == null ? "Nova Transação" : "Editar Transação");
            return "transacoes/form";
        }

        //se for edição, preserva os comentários
        if (transacao.getId() != null) {
            Transacao existente = transacaoService.findById(transacao.getId()).orElseThrow();
            transacao.setComentarios(existente.getComentarios());
        }

        transacaoService.save(transacao);
        return "redirect:/transacoes";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id,
                         Model model,
                         HttpSession session) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");
        Transacao transacao = transacaoService.findById(id).orElseThrow();

        if (usuario == null || !transacao.getConta().getCorrentista().getId().equals(usuario.getId())) {
            return "redirect:/transacoes";
        }

        model.addAttribute("transacao", transacao);
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
        model.addAttribute("titulo", "Editar Transação");
        return "transacoes/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        transacaoService.deleteById(id);
        return "redirect:/transacoes";
    }
}

