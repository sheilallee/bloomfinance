package br.edu.ifpb.pweb2.bloomfinance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import br.edu.ifpb.pweb2.bloomfinance.service.CategoriaService;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categoriaService.findAll());
        model.addAttribute("titulo", "Categorias");
        return "categorias/list";
    }

    @GetMapping("/form")
    public String form(Model model) {
        model.addAttribute("categoria", new Categoria());
        model.addAttribute("titulo", "Nova Categoria");
        return "categorias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Categoria categoria) {
        categoriaService.save(categoria);
        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Categoria categoria = categoriaService.findById(id).orElseThrow();
        model.addAttribute("categoria", categoria);
        model.addAttribute("titulo", "Editar Categoria");
        return "categorias/form";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        categoriaService.deleteById(id);
        return "redirect:/categorias";
    }
}
