package br.edu.ifpb.pweb2.bloomfinance.controller;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.bloomfinance.dto.CorrentistaForm;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;
import br.edu.ifpb.pweb2.bloomfinance.service.CorrentistaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/correntistas")
@RequiredArgsConstructor
public class CorrentistaController {

    private final CorrentistaService correntistaService;
    private final UserRepository userRepository;

    @GetMapping
    public String listar(Model model,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Correntista> correntistasPage = correntistaService.listarPaginadoOrdenadoPorIdDesc(pageable);

        model.addAttribute("titulo", "Listagem de Correntistas");
        model.addAttribute("correntistas", correntistasPage);
        return "correntistas/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("titulo", "Novo Correntista");
        model.addAttribute("correntista", new CorrentistaForm());
        return "correntistas/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Correntista existente = correntistaService.findById(id).orElseThrow();

        CorrentistaForm form = new CorrentistaForm();
        form.setId(existente.getId());
        form.setNome(existente.getNome());
        form.setEmail(existente.getEmail());
        form.setAdmin(existente.isAdmin());
        form.setBloqueado(existente.isBloqueado());

        User u = existente.getUser();
        form.setUsername(u.getUsername());
        form.setSenha(""); // senha NÃO é exibida; troca apenas se preencher

        model.addAttribute("correntista", form);
        model.addAttribute("titulo", "Editar Correntista");
        return "correntistas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("correntista") CorrentistaForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes ra) {

        boolean novo = (form.getId() == null);

        // senha obrigatória no cadastro
        // if (novo && (form.getSenha() == null || form.getSenha().isBlank())) {
        //     result.rejectValue("senha", "senha.obrigatoria", "A senha é obrigatória no cadastro.");
        // }
        if (novo) {
            if (form.getSenha() == null || form.getSenha().isBlank()) {
                result.rejectValue("senha", "senha.obrigatoria", "A senha é obrigatória no cadastro.");
            } else if (!form.getSenha().matches("^(?=.*\\d).{8,}$")) {
                // Regex: pelo menos 8 caracteres e pelo menos 1 número
                result.rejectValue("senha", "senha.invalida", "A senha deve ter no mínimo 8 caracteres e conter pelo menos um número.");
            }
        } else if (form.getSenha() != null && !form.getSenha().isBlank()) {
            if (!form.getSenha().matches("^(?=.*\\d).{8,}$")) {
                result.rejectValue("senha", "senha.invalida", "A senha deve ter no mínimo 8 caracteres e conter pelo menos um número.");
            }
        }


        // username único: no novo, não pode existir; na edição, não pode pertencer a outro user
        if (novo) {
            if (userRepository.existsByUsername(form.getUsername())) {
                result.rejectValue("username", "username.duplicado", "Este username já está em uso.");
            }
        } else {
            Correntista atual = correntistaService.findById(form.getId()).orElseThrow();
            String usernameAtual = atual.getUser().getUsername();

            if (!usernameAtual.equals(form.getUsername())
                    && userRepository.existsByUsername(form.getUsername())) {
                result.rejectValue("username", "username.duplicado", "Este username já está em uso.");
            }
        }

        //email único
        if (novo) {
            if (correntistaService.existsByEmail(form.getEmail())) {
                result.rejectValue("email", "email.duplicado", "Este email já está em uso.");
            }
        } else {
            Correntista atual = correntistaService.findById(form.getId()).orElseThrow();
            String emailAtual = atual.getEmail();
            if (!emailAtual.equals(form.getEmail())
                    && correntistaService.existsByEmail(form.getEmail())) {
                result.rejectValue("email", "email.duplicado", "Este email já está em uso.");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("titulo", novo ? "Cadastro de Correntista" : "Editar Correntista");
            return "correntistas/form";
        }

        if (novo) {
            correntistaService.criarComUserERoles(form);
            ra.addFlashAttribute("mensagem", "Correntista criado com sucesso.");
        } else {
            correntistaService.atualizarComUserERoles(form);
            ra.addFlashAttribute("mensagem", "Correntista atualizado com sucesso.");
        }

        return "redirect:/correntistas";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String mensagem = correntistaService.excluirComUser(id);
        redirectAttributes.addFlashAttribute("mensagem", mensagem);
        return "redirect:/correntistas";
    }
}



