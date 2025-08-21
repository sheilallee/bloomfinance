package br.edu.ifpb.pweb2.bloomfinance.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.bloomfinance.dto.CorrentistaForm;
import br.edu.ifpb.pweb2.bloomfinance.model.Authority;
import br.edu.ifpb.pweb2.bloomfinance.model.Correntista;
import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.CorrentistaRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CorrentistaService {

    private final CorrentistaRepository correntistaRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    public List<Correntista> findAll() {
        return correntistaRepository.findAll();
    }

    public Optional<Correntista> findById(Long id) {
        return correntistaRepository.findById(id);
    }

    public Optional<Correntista> findByEmail(String email) {
        return correntistaRepository.findByEmail(email);
    }

    public Page<Correntista> listarPaginadoOrdenadoPorIdDesc(Pageable pageable) {
        return correntistaRepository.findAll(pageable);
    }

    //    CRIAÇÃO    
    @Transactional
    public Correntista criarComUserERoles(CorrentistaForm dto) {
        // cria user
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getSenha()))
                .enabled(!dto.isBloqueado())
                .build();
        user = userRepository.save(user);

        // roles
        garantirRoleUser(user);
        aplicarRoleAdmin(user, dto.isAdmin());

        // cria correntista vinculando user
        Correntista c = new Correntista();
        c.setNome(dto.getNome());
        c.setEmail(dto.getEmail());
        c.setAdmin(dto.isAdmin());
        c.setBloqueado(dto.isBloqueado());
        c.setUser(user);

        return correntistaRepository.save(c);
    }

    //    ATUALIZAÇÃO  
    @Transactional
    public Correntista atualizarComUserERoles(CorrentistaForm dto) {
        Correntista existente = correntistaRepository.findById(dto.getId()).orElseThrow();
        User user = existente.getUser();

        // dados do correntista
        existente.setNome(dto.getNome());
        existente.setEmail(dto.getEmail());
        existente.setAdmin(dto.isAdmin());
        existente.setBloqueado(dto.isBloqueado());

        // dados do user
        if (!Objects.equals(user.getUsername(), dto.getUsername())) {
            user.setUsername(dto.getUsername());
        }
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getSenha()));
        }
        user.setEnabled(!dto.isBloqueado());
        userRepository.save(user);

        // roles
        garantirRoleUser(user);
        aplicarRoleAdmin(user, dto.isAdmin());

        return correntistaRepository.save(existente);
    }

    //     EXCLUSÃO 
    @Transactional
    public String excluirComUser(Long correntistaId) {
        Optional<Correntista> correntistaOpt = correntistaRepository.findById(correntistaId);
        if (correntistaOpt.isEmpty()) return "Correntista não encontrado.";

        Correntista c = correntistaOpt.get();
        User u = c.getUser();

        // remove correntista (tem FK para users)
        correntistaRepository.delete(c);

        // remove user (cascade ALL + orphanRemoval em User.authorities - apaga authorities)
        userRepository.delete(u);

        return "Correntista e usuário removidos com sucesso.";
    }

    //   roles 
    private void garantirRoleUser(User user) {
        boolean temUser = authorityRepository.findByUser(user).stream()
                .anyMatch(a -> "ROLE_USER".equals(a.getAuthority()));
        if (!temUser) {
            authorityRepository.save(Authority.builder()
                    .user(user)
                    .authority("ROLE_USER")
                    .build());
        }
    }

    private void aplicarRoleAdmin(User user, boolean admin) {
        var roles = authorityRepository.findByUser(user);
        boolean temAdmin = roles.stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (admin && !temAdmin) {
            authorityRepository.save(Authority.builder()
                    .user(user)
                    .authority("ROLE_ADMIN")
                    .build());
        }
        if (!admin && temAdmin) {
            // remove apenas a ROLE_ADMIN (mantém ROLE_USER)
            roles.stream()
                 .filter(a -> "ROLE_ADMIN".equals(a.getAuthority()))
                 .forEach(authorityRepository::delete);
        }
    }
}
