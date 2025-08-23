package br.edu.ifpb.pweb2.bloomfinance.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.service.OrcamentoService;
import br.edu.ifpb.pweb2.bloomfinance.util.SecurityUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrcamentoController {

    private final OrcamentoService orcamentoService;
    private final SecurityUtil securityUtil;

    @GetMapping("/orcamento")
    public String listar(@RequestParam(name = "ano", required = false) Integer ano,
                         Model model) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        int anoAlvo = (ano != null) ? ano : LocalDate.now().getYear();

        Map<String, Object> dados = orcamentoService.montarOrcamento(usuario.getId(), anoAlvo);

        model.addAllAttributes(dados);
        model.addAttribute("titulo", "Orçamento");
        return "orcamento/list";
    }
}
