package br.edu.ifpb.pweb2.bloomfinance.config;

import br.edu.ifpb.pweb2.bloomfinance.model.Authority;
import br.edu.ifpb.pweb2.bloomfinance.model.User;
import br.edu.ifpb.pweb2.bloomfinance.repository.AuthorityRepository;
import br.edu.ifpb.pweb2.bloomfinance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.stream.Collectors;

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
                .requestMatchers("/", "/home", "/auth/login", "/css/**", "/js/**", "/imagens/**", "/favicon.ico").permitAll()
                // Rotas administrativas (ROLE_ADMIN)
                .requestMatchers("/correntistas/**", "/categorias/**").hasRole("ADMIN")
                // Demais rotas precisam de login
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/auth/login").permitAll()
                .loginProcessingUrl("/auth/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/auth/login?error")
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout", "POST"))
                .logoutSuccessUrl("/auth/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
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
