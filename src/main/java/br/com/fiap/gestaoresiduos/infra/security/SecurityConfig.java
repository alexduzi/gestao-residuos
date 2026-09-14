package br.com.fiap.gestaoresiduos.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/register", "/api/v1/auth/login").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/h2-console/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()

                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pontos-coleta/*/volume").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/notificacoes/*/lida").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/v1/alertas", "/api/v1/alertas/pendentes").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tipos-residuo/**", "/api/v1/pontos-coleta/**", "/api/v1/coletas/**", "/api/v1/alertas/**", "/api/v1/notificacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/tipos-residuo/**", "/api/v1/pontos-coleta/**", "/api/v1/coletas/**", "/api/v1/alertas/**", "/api/v1/notificacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/pontos-coleta/**", "/api/v1/coletas/**", "/api/v1/alertas/**", "/api/v1/notificacoes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/tipos-residuo/**", "/api/v1/pontos-coleta/**", "/api/v1/coletas/**", "/api/v1/alertas/**", "/api/v1/notificacoes/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .userDetailsService(userDetailsService)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
