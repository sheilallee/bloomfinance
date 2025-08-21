package br.edu.ifpb.pweb2.bloomfinance.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.edu.ifpb.pweb2.bloomfinance.model.Authority;
import br.edu.ifpb.pweb2.bloomfinance.model.Categoria;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.NaturezaCategoria;
import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.CategoriaRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;

@Component
public class InicializadorCorrentista implements ApplicationRunner {

    @Autowired private CorrentistaRepository correntistaRepository;
    @Autowired private CategoriaRepository categoriaRepository;

    @Autowired private UserRepository userRepository;
    @Autowired private AuthorityRepository authorityRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {

        // 1) USER/ROLES admin
        User adminUser = userRepository.findByUsername("admin")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .enabled(true)
                        .build()));

        if (authorityRepository.findByUser(adminUser).isEmpty()) {
            authorityRepository.save(Authority.builder().user(adminUser).authority("ROLE_ADMIN").build());
            authorityRepository.save(Authority.builder().user(adminUser).authority("ROLE_USER").build());
        }

        // 2) Correntista Administrador vinculado ao User
        correntistaRepository.findByEmail("admin@bloomfinance.com").orElseGet(() -> {
            Correntista admin = new Correntista();
            admin.setNome("Administrador");
            admin.setEmail("admin@bloomfinance.com");
            admin.setAdmin(true);
            admin.setBloqueado(false);
            admin.setUser(adminUser);
            return correntistaRepository.save(admin);
        });

        // 3) Categorias padrão
        if (categoriaRepository.count() == 0) {
            List<Categoria> categorias = Arrays.asList(
                novaCategoria("Salário", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Cashback", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Resgate Investimento", NaturezaCategoria.ENTRADA, 1),
                novaCategoria("Outras Entradas", NaturezaCategoria.ENTRADA, 1),

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

                novaCategoria("Aporte Renda Fixa", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Renda Variável", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Reserva Emergencia", NaturezaCategoria.INVESTIMENTO, 3),
                novaCategoria("Aporte Previdência", NaturezaCategoria.INVESTIMENTO, 3)
            );
            categoriaRepository.saveAll(categorias);
            System.out.println("Categorias predefinidas criadas com sucesso!");
        }
    }

    private Categoria novaCategoria(String nome, NaturezaCategoria natureza, int ordemNatureza) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setNatureza(natureza);
        c.setAtiva(true);
        c.setOrdem(ordemNatureza);
        return c;
    }
}
