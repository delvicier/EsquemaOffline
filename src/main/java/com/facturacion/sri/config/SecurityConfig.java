package com.facturacion.sri.config;

import com.facturacion.sri.entity.UsuarioSistema;
import com.facturacion.sri.repository.UsuarioSistemaRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioSistemaRepository usuarioRepository) {
        return username -> {
            UsuarioSistema u = usuarioRepository.findByUsernameAndActivoTrue(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado o inactivo: " + username));

            return User.withUsername(u.getUsername())
                    .password(u.getPasswordHash())
                    .authorities(u.getRol())
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/**").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint())
                        .accessDeniedHandler(customAccessDeniedHandler())
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            String json = """
                    {
                      "timestamp": "%s",
                      "status": 401,
                      "error": "No Autorizado (Unauthorized)",
                      "mensaje": "Credenciales de acceso inválidas o no proporcionadas. Envíe su usuario y contraseña mediante Basic Auth.",
                      "ruta": "%s"
                    }
                    """.formatted(LocalDateTime.now().toString(), request.getRequestURI());

            response.getWriter().write(json);
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json;charset=UTF-8");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());

            String json = """
                    {
                      "timestamp": "%s",
                      "status": 403,
                      "error": "Acceso Prohibido (Forbidden)",
                      "mensaje": "Su usuario está autenticado pero no tiene los permisos necesarios para ejecutar esta operación.",
                      "ruta": "%s"
                    }
                    """.formatted(LocalDateTime.now().toString(), request.getRequestURI());

            response.getWriter().write(json);
        };
    }
}
