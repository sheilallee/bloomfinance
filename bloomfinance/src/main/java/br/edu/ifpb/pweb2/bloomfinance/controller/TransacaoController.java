package br.edu.ifpb.pweb2.bloomfinance.controller;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
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
import br.edu.ifpb.pweb2.bloomfinance.util.SecurityUtil;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    @Autowired private TransacaoService transacaoService;
    @Autowired private ContaService contaService;
    @Autowired private CategoriaService categoriaService;
    @Autowired private SecurityUtil securityUtil;

    /** Converte BigDecimal usando pt-BR (1.234,56) tanto para bind quanto para exibição */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null) { setValue(null); return; }
                String v = text.trim();
                if (v.isEmpty()) { setValue(null); return; }
                // remove separador de milhar e troca vírgula por ponto
                v = v.replace(".", "").replace(",", ".");
                try {
                    setValue(new BigDecimal(v));
                } catch (NumberFormatException e) {
                    setValue(null);
                }
            }
            @Override
            public String getAsText() {
                BigDecimal val = (BigDecimal) getValue();
                if (val == null) return "";
                DecimalFormatSymbols sy = new DecimalFormatSymbols(new Locale("pt","BR"));
                DecimalFormat df = new DecimalFormat("#,##0.##", sy);
                return df.format(val);
            }
        });
    }

    @GetMapping
    public String listar(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Pageable pageable = PageRequest.of(page, size);
        Page<Transacao> transacoesPage = transacaoService.findByCorrentista(usuario.getId(), pageable);

        model.addAttribute("transacoes", transacoesPage);
        model.addAttribute("titulo", "Minhas Transações");
        return "transacoes/list";
    }

    @GetMapping("/form")
    public String form(Model model, @RequestParam(required = false) Long contaId) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Transacao t = new Transacao();
        if (contaId != null) {
            Conta conta = contaService.findById(contaId).orElseThrow();
            if (!conta.getCorrentista().getId().equals(usuario.getId())) {
                return "redirect:/transacoes";
            }
            t.setConta(conta);
        }

        model.addAttribute("transacao", t);
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
        model.addAttribute("titulo", "Nova Transação");
        return "transacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Transacao transacao,
                         BindingResult result,
                         Model model) {

        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        // Conta vem do form só com o ID - recarregar e validar 
        if (transacao.getConta() == null || transacao.getConta().getId() == null) {
            result.rejectValue("conta", "conta.obrigatoria", "Selecione a conta.");
        } else {
            Conta conta = contaService.findById(transacao.getConta().getId()).orElse(null);
            if (conta == null) {
                result.rejectValue("conta", "conta.invalida", "Conta inválida.");
            } else if (!conta.getCorrentista().getId().equals(usuario.getId())) {
                return "redirect:/transacoes";
            } else {
                transacao.setConta(conta);
            }
        }

        // Validação do valor já convertido pelo binder
        if (transacao.getValor() == null) {
            result.rejectValue("valor", "valor.obrigatorio", "O valor é obrigatório.");
        } else if (transacao.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            result.rejectValue("valor", "valor.min", "O valor deve ser maior que zero.");
        }

        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.findAtivas());
            model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
            model.addAttribute("titulo", transacao.getId() == null ? "Nova Transação" : "Editar Transação");
            return "transacoes/form";
        }

        // Preserva comentários em edição
        if (transacao.getId() != null) {
            Transacao existente = transacaoService.findById(transacao.getId()).orElseThrow();
            transacao.setComentarios(existente.getComentarios());
        }

        transacaoService.save(transacao);
        return "redirect:/transacoes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Transacao t = transacaoService.findById(id).orElseThrow();
        if (!t.getConta().getCorrentista().getId().equals(usuario.getId())) {
            return "redirect:/transacoes";
        }

        model.addAttribute("transacao", t);
        model.addAttribute("categorias", categoriaService.findAtivas());
        model.addAttribute("contas", contaService.findByCorrentistaId(usuario.getId()));
        model.addAttribute("titulo", "Editar Transação");
        return "transacoes/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Transacao t = transacaoService.findById(id).orElse(null);
        if (t != null && t.getConta().getCorrentista().getId().equals(usuario.getId())) {
            transacaoService.deleteById(id);
        }
        return "redirect:/transacoes";
    }
}



