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
import br.edu.ifpb.pweb2.bloomfinance.util.SecurityUtil;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/contas")
public class ContaController {

    @Autowired private ContaService contaService;
    @Autowired private SecurityUtil securityUtil;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {

        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, size);
        Page<Conta> contasPage = contaService.findByCorrentistaId(usuario.getId(), pageable);

        model.addAttribute("titulo", "Minhas Contas");
        model.addAttribute("contas", contasPage);
        return "contas/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("conta", new Conta());
        return "contas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Conta conta, BindingResult result, Model model) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        if (result.hasErrors()) {
            model.addAttribute("conta", conta);
            return "contas/form";
        }

        // força o vínculo da conta ao correntista logado
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
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Conta conta = contaService.findById(id).orElse(null);
        if (conta == null || !conta.getCorrentista().getId().equals(usuario.getId())) {
            // evita editar conta de outro usuário
            return "redirect:/contas";
        }

        model.addAttribute("conta", conta);
        model.addAttribute("titulo", "Editar Conta");
        return "contas/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Conta conta = contaService.findById(id).orElse(null);
        if (conta != null && conta.getCorrentista().getId().equals(usuario.getId())) {
            contaService.deleteById(id);
        }
        return "redirect:/contas";
    }
}
