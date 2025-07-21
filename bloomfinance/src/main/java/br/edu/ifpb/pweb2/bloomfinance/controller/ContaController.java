package br.edu.ifpb.pweb2.bloomfinance.controller;


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
import br.edu.ifpb.pweb2.bloomfinance.service.ContaService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired
    private ContaService contaService;

    @GetMapping
    public String listar(HttpSession session, Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size) {

        Correntista usuario = (Correntista) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth";
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Conta> contasPage = contaService.findByCorrentistaId(usuario.getId(), pageable);

        model.addAttribute("titulo", "Minhas Contas");
        model.addAttribute("contas", contasPage);

        return "contas/list";
    }

    /*@GetMapping
    public String listar(HttpSession session, Model model) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");
        model.addAttribute("titulo", "Minhas Contas");
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId().longValue()));
        return "contas/list";
    }*/
    /*@GetMapping
    public String listar(HttpSession session, Model model) {
        Correntista usuario = (Correntista) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth"; // Evita erro se usuário não estiver logado
        }

        model.addAttribute("titulo", "Minhas Contas");
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
        return "contas/list";
    }*/

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("conta", new Conta());
        //model.addAttribute("titulo", "Nova Conta");
        return "contas/form";
    }

    
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Conta conta, BindingResult result, HttpSession session, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("conta", conta);
            return "contas/form";
        }

        Correntista usuario = (Correntista) session.getAttribute("usuario");
        conta.setCorrentista(usuario);

        try {
            contaService.save(conta);
            return "redirect:/contas";
        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "contas/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Conta conta = contaService.findById(id).orElseThrow();
        model.addAttribute("conta", conta);
        model.addAttribute("titulo", "Editar Conta");
        return "contas/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        contaService.deleteById(id);
        return "redirect:/contas";
    }

}



