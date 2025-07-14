package br.edu.ifpb.pweb2.bloomfinance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.bloomfinance.util.PasswordUtil;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;


@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @GetMapping
    public ModelAndView getForm(ModelAndView model) {
        model.setViewName("auth/login");
        model.addObject("usuario", new Correntista());
        return model;
    }

    @PostMapping
    public ModelAndView valide(Correntista correntista, HttpSession session, ModelAndView model,
            RedirectAttributes redirectAttts) {
        if ((correntista = this.isValido(correntista)) != null) {
            session.setAttribute("usuario", correntista);
            model.setViewName("redirect:/home");
        } else {
            redirectAttts.addFlashAttribute("mensagem", "E-mail e/ou senha inválidos!");
            model.setViewName("redirect:/auth");
        }
        return model;
    }

    @GetMapping("/logout")
    public ModelAndView logout(ModelAndView mav, HttpSession session) {
        session.invalidate();
        mav.setViewName("redirect:/auth");
        return mav;
    }

    private Correntista isValido(Correntista correntista) {
        Optional<Correntista> optional = correntistaRepository.findByEmail(correntista.getEmail());

        if (optional.isPresent()) {
            Correntista correntistaBD = optional.get();
            
            // Verifica se a senha informada confere com o hash salvo
            if (PasswordUtil.checkPassword(correntista.getSenha(), correntistaBD.getSenha())) {
                return correntistaBD;
            }
        }

        return null;
    }


}




