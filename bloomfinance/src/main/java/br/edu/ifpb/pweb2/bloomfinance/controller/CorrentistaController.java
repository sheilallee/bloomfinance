package br.edu.ifpb.pweb2.bloomfinance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.service.CorrentistaService;
import br.edu.ifpb.pweb2.bloomfinance.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


@Controller
@RequestMapping("/correntistas")
public class CorrentistaController {

    @Autowired
    private CorrentistaService correntistaService;

    @GetMapping
    public String listar(Model model,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        HttpSession session) {

        Correntista usuario = (Correntista) session.getAttribute("usuario");

        if (usuario == null || !usuario.isAdmin()) {
            return "redirect:/auth";
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Correntista> correntistasPage = correntistaService.listarPaginadoOrdenadoPorIdDesc(pageable);

        model.addAttribute("titulo", "Listagem de Correntistas");
        model.addAttribute("correntistas", correntistasPage);

        return "correntistas/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("correntista", new Correntista());
        model.addAttribute("titulo", "Novo Correntista");
        return "correntistas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Correntista correntista, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", correntista.getId() == null ? "Novo Correntista" : "Editar Correntista");
            return "correntistas/form";
        }

        if (correntista.getId() != null) {
            
            Correntista existente = correntistaService.findById(correntista.getId()).orElseThrow();

            existente.setNome(correntista.getNome());
            existente.setEmail(correntista.getEmail());
            existente.setAdmin(correntista.isAdmin());
            existente.setBloqueado(correntista.isBloqueado());

            if (correntista.getSenha() != null && !correntista.getSenha().isEmpty()) {
                existente.setSenha(PasswordUtil.hashPassword(correntista.getSenha()));
            }

            correntistaService.save(existente);
        } else {
            correntista.setSenha(PasswordUtil.hashPassword(correntista.getSenha()));
            correntistaService.save(correntista);
        }

        return "redirect:/correntistas";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Correntista correntista = correntistaService.findById(id).orElseThrow();
        model.addAttribute("correntista", correntista);
        model.addAttribute("titulo", "Editar Correntista");
        return "correntistas/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        correntistaService.deleteById(id);
        return "redirect:/correntistas";
    }
}
