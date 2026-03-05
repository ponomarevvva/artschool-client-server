package ru.artschool.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.artschool.server.repository.UserRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository repo) {
        return username -> repo.findByUsername(username)
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPasswordHash())
                        .roles(u.getRole().name()) // ROLE_ADMIN / ROLE_USER
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());

        // нужно для H2 Console (если пользуешься)
        http.headers(h -> h.frameOptions(f -> f.sameOrigin()));

        http.authorizeHttpRequests(auth -> auth
                // регистрация без логина
                .requestMatchers("/auth/register").permitAll()
                // h2 console (по желанию)
                .requestMatchers("/h2-console/**").permitAll()

                // просмотр доступен USER и ADMIN
                .requestMatchers(HttpMethod.GET, "/students/**", "/courses/**", "/stats/**", "/auth/me").hasAnyRole("USER", "ADMIN")

                // изменения только ADMIN
                .requestMatchers("/students/**", "/courses/**").hasRole("ADMIN")

                // всё остальное — только авторизованным
                .anyRequest().authenticated()
        );

        http.httpBasic(Customizer.withDefaults());
        return http.build();
    }
}