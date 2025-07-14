package br.edu.ifpb.pweb2.bloomfinance.config;


import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.bloomfinance.util.PasswordUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InicializadorCorrentista implements ApplicationRunner {

    @Autowired
    private CorrentistaRepository correntistaRepository;

    @Override
    public void run(ApplicationArguments args) {
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
    }
}
