package br.edu.ifpb.pweb2.bloomfinance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.bloomfinance.model.Comentario;
import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import br.edu.ifpb.pweb2.bloomfinance.service.ComentarioService;
import br.edu.ifpb.pweb2.bloomfinance.service.TransacaoService;

@Controller
@RequestMapping("/comentarios")
public class ComentarioController {

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private TransacaoService transacaoService;

    //lista comentários de uma transação
    @GetMapping("/transacao/{transacaoId}")
    public String listarComentarios(@PathVariable Long transacaoId, Model model) {
        Transacao transacao = transacaoService.findById(transacaoId).orElseThrow();
        model.addAttribute("transacao", transacao);
        model.addAttribute("comentarios", comentarioService.findByTransacaoId(transacaoId));
        return "comentarios/list"; 
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Long transacaoId, @RequestParam String texto) {
        Transacao transacao = transacaoService.findById(transacaoId).orElseThrow();
        Comentario comentario = new Comentario();
        comentario.setTexto(texto);
        comentario.setTransacao(transacao);
        comentarioService.save(comentario);
        return "redirect:/comentarios/transacao/" + transacaoId; 
    }

    @PostMapping("/editar")
    public String editar(@RequestParam Long id, @RequestParam String texto) {
        Comentario comentario = comentarioService.findById(id).orElseThrow();
        comentario.setTexto(texto);
        comentarioService.save(comentario);
        return "redirect:/comentarios/transacao/" + comentario.getTransacao().getId();
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {
        Comentario comentario = comentarioService.findById(id).orElseThrow();
        Long transacaoId = comentario.getTransacao().getId();
        comentarioService.deleteById(id);
        return "redirect:/comentarios/transacao/" + transacaoId;
    }

    @GetMapping("/form/{transacaoId}")
    public String form(@PathVariable Long transacaoId, Model model) {
        Transacao transacao = transacaoService.findById(transacaoId).orElseThrow();
        model.addAttribute("transacao", transacao);
        return "comentarios/form";
    }
}
