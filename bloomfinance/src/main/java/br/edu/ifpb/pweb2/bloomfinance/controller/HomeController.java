package br.edu.ifpb.pweb2.bloomfinance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("titulo", "Bem-vindo");
        return "index";
    }
}

