package br.edu.ifpb.pweb2.bloomfinance.config;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.edu.ifpb.pweb2.bloomfinance.model.Authority;
import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(reg -> reg
                //público
                .requestMatchers("/", "/auth/login", "/css/**", "/js/**", "/imagens/**", "/favicon.ico").permitAll()

                //apenas ADMIN pode gerenciar correntistas e categorias
                .requestMatchers("/correntistas/**", "/categorias/**").hasRole("ADMIN")
                .requestMatchers("/contas/form").hasRole("ADMIN")

                //ADMIN e USER Correntista podem acessar contas, transações, comentários, extrato e orçamento
                .requestMatchers("/contas/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/transacoes/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/comentarios/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/extratos/**").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/orcamento/**").hasAnyRole("ADMIN", "USER")

                //qualquer outra rota exige login
                .requestMatchers("/home").authenticated()
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/auth/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
             .exceptionHandling(ex -> ex
                .accessDeniedPage("/auth/acesso-negado")
            )
            .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

            var roles = authorityRepository.findByUser(user).stream()
                    .map(Authority::getAuthority)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getEnabled(),
                    true, true, true,
                    roles
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder pe) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(pe);
        return provider;
    }
}