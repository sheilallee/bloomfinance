package br.edu.ifpb.pweb2.bloomfinance.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    private final UserRepository userRepository;

    public HomeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Authentication authentication, Model model, HttpSession session) {
        //pega o username do usuário autenticado
        String username = authentication.getName();

        //busca o usuário completo no banco
        User usuario = userRepository.findByUsername(username).orElse(null);

        //adiciona na sessão para uso em home.html
        session.setAttribute("usuario", usuario);

        String titulo = "Bem-vindo";
        if (usuario != null) {
            titulo += " " + usuario.getUsername();
        }
        model.addAttribute("titulo", titulo);

        return "home"; 
    }
}