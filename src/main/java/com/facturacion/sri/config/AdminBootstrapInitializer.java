package com.facturacion.sri.config;

import com.facturacion.sri.entity.UsuarioSistema;
import com.facturacion.sri.repository.UsuarioSistemaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminBootstrapInitializer implements CommandLineRunner {

    private final UsuarioSistemaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username:admin}")
    private String initialAdminUser;

    @Value("${app.admin.password:AdminTemporal2026*}")
    private String initialAdminPass;

    public AdminBootstrapInitializer(UsuarioSistemaRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            UsuarioSistema admin = new UsuarioSistema();
            admin.setUsername(initialAdminUser.trim());
            admin.setPasswordHash(passwordEncoder.encode(initialAdminPass));
            admin.setRol("ROLE_ADMIN");
            admin.setRequiereCambioClave(true);
            admin.setActivo(true);
            admin.setFechaActualizacion(LocalDateTime.now());

            usuarioRepository.save(admin);
            System.out.println(">>> [SEGURIDAD] Usuario administrador inicial creado desde .env: " + initialAdminUser);
            System.out.println(">>> [SEGURIDAD] Recuerde cambiar la contraseña inicial en PUT /api/v1/auth/cambiar-credenciales");
        }
    }
}