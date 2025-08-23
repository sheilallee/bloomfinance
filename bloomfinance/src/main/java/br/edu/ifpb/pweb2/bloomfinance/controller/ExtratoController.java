package br.edu.ifpb.pweb2.bloomfinance.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.edu.ifpb.pweb2.bloomfinance.dto.LinhaExtratoDTO;
import br.edu.ifpb.pweb2.bloomfinance.model.Conta;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.Movimento;
import br.edu.ifpb.pweb2.bloomfinance.model.Transacao;
import br.edu.ifpb.pweb2.bloomfinance.service.ContaService;
import br.edu.ifpb.pweb2.bloomfinance.service.TransacaoService;
import br.edu.ifpb.pweb2.bloomfinance.util.SecurityUtil;

@Controller
@RequestMapping("/extratos")
public class ExtratoController {

    @Autowired private ContaService contaService;
    @Autowired private TransacaoService transacaoService;
    @Autowired private SecurityUtil securityUtil;

        @GetMapping
        public String escolherConta(Model model) {
                Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
                if (usuario == null) return "redirect:/auth/login";

                List<Conta> contas = contaService.findByCorrentistaId(usuario.getId());

                model.addAttribute("contas", contas);
                return "extratos/escolherConta"; 
                }


        @GetMapping("/{contaId}")
        public String extrato(@PathVariable Long contaId,
                        @RequestParam(required = false) String inicio,
                        @RequestParam(required = false) String fim,
                        @RequestParam(required = false) String mes,
                        Model model) {

        Correntista usuario = securityUtil.getCorrentistaAutenticadoOrNull();
        if (usuario == null) return "redirect:/auth/login";

        Conta conta = contaService.findById(contaId).orElse(null);
        if (conta == null || !conta.getCorrentista().getId().equals(usuario.getId())) {
                return "redirect:/contas";
        }

        LocalDate dataInicio;
        LocalDate dataFim;

        if (mes != null && !mes.isEmpty()) {
                int mesInt = Integer.parseInt(mes);
                LocalDate agora = LocalDate.now();
                dataInicio = LocalDate.of(agora.getYear(), mesInt, 1);
                dataFim = dataInicio.withDayOfMonth(dataInicio.lengthOfMonth());
        } else {
                dataInicio = (inicio != null && !inicio.isEmpty())
                        ? LocalDate.parse(inicio)
                        : LocalDate.now().withDayOfMonth(1);

                dataFim = (fim != null && !fim.isEmpty())
                        ? LocalDate.parse(fim)
                        : LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        }

        List<Transacao> transacoes = transacaoService.findByContaIdAndDataBetween(contaId, dataInicio, dataFim);

        // saldo inicial (antes do período)
        BigDecimal saldoInicial = conta.getTransacoes().stream()
                .filter(t -> t.getData().isBefore(dataInicio))
                .map(t -> t.getMovimento() == Movimento.CREDITO
                        ? t.getValor()
                        : t.getValor().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // montar linhas com saldo acumulado
        BigDecimal saldoAcumulado = saldoInicial;
        List<LinhaExtratoDTO> linhas = new java.util.ArrayList<>();
        for (Transacao t : transacoes) {
                BigDecimal valor = t.getMovimento() == Movimento.CREDITO
                        ? t.getValor()
                        : t.getValor().negate();
                saldoAcumulado = saldoAcumulado.add(valor);

                linhas.add(new LinhaExtratoDTO(
                        t.getData(),
                        t.getCategoria() != null ? t.getCategoria().getNome() : "-",
                        t.getDescricao(),
                        t.getValor(),
                        t.getMovimento(),
                        t.getId(), 
                        saldoAcumulado
                ));
        }

        BigDecimal saldoFinal = saldoAcumulado;

        model.addAttribute("conta", conta);
        model.addAttribute("linhas", linhas);
        model.addAttribute("saldoInicial", saldoInicial);
        model.addAttribute("saldoFinal", saldoFinal);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("mesSelecionado", mes);

        return "extratos/list";
        }

}