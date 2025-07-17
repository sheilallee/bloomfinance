package br.edu.ifpb.pweb2.bloomfinance.config;

import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.NaturezaCategoria;
import br.edu.ifpb.pweb2.bloomfinance.repository.CategoriaRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.bloomfinance.util.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class InicializadorCorrentista implements ApplicationRunner {

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Override
    public void run(ApplicationArguments args) {

        // Criar administrador
        if (correntistaRepository.findByEmail("admin@bloomfinance.com").isEmpty()) {
            Correntista admin = new Correntista();
            admin.setNome("Administrador");
            admin.setEmail("admin@bloomfinance.com");
            admin.setSenha(PasswordUtil.hashPassword("123"));
            admin.setAdmin(true);
            admin.setBloqueado(false);
            correntistaRepository.save(admin);
            System.out.println("Administrador criado com sucesso!");
        } else {
            System.out.println("Administrador já existe.");
        }

        // Criar categorias predefinidas
        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = Arrays.asList(
                // ENTRADA - ordem = 1
                novaCategoria("Salário", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Cashback", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Resgate Investimento", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Outras Entradas", NaturezaCategoria.ENTRADA, 1),

                // SAÍDA - ordem = 2
                novaCategoria("Saúde e Remédios", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Academia e Personal", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Carros e Uber", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Educação e Cursos", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Lazer e Turismo", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Condomínio", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Energia", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Celular", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Internet", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Itens Pessoais", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Feira", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Casa", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Impostos", NaturezaCategoria.SAIDA, 2),
                novaCategoria("Outros gastos", NaturezaCategoria.SAIDA, 2),

                // INVESTIMENTO - ordem = 3
                novaCategoria("Aporte Renda Fixa", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Renda Variável", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Reserva Emergencia", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Previdência", NaturezaCategoria.INVESTIMENTO, 3)
            );

            categoriaRepository.saveAll(categorias);
            System.out.println("Categorias predefinidas criadas com sucesso!");
        } else {
            System.out.println("Categorias já existentes.");
        }
    }

    private Categoria novaCategoria(String nome, NaturezaCategoria natureza, int ordemNatureza) {
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        categoria.setNatureza(natureza);
        categoria.setAtiva(true);
        categoria.setOrdem(ordemNatureza);
        return categoria;
    }
}






