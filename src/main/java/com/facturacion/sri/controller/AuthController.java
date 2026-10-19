package com.facturacion.sri.controller;

import com.facturacion.sri.dto.CambiarCredencialesRequestDto;
import com.facturacion.sri.entity.UsuarioSistema;
import com.facturacion.sri.repository.UsuarioSistemaRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UsuarioSistemaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UsuarioSistemaRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Permite al administrador cambiar su nombre de usuario y contraseña inicial del .env
     * por credenciales definitivas encriptadas con BCrypt en PostgreSQL.
     */
    @PutMapping("/cambiar-credenciales")
    @Transactional
    public ResponseEntity<Map<String, Object>> cambiarCredenciales(
            Authentication authentication,
            @Valid @RequestBody CambiarCredencialesRequestDto request) {

        String usernameActual = authentication.getName();
        UsuarioSistema usuario = usuarioRepository.findByUsernameAndActivoTrue(usernameActual)
                .orElseThrow(() -> new IllegalArgumentException("Usuario autenticado no encontrado."));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual ingresada es incorrecta.");
        }

        String nuevoUser = request.getNuevoUsername().trim();
        if (!nuevoUser.equalsIgnoreCase(usernameActual) &&
                usuarioRepository.findByUsernameAndActivoTrue(nuevoUser).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario '" + nuevoUser + "' ya está en uso.");
        }

        usuario.setUsername(nuevoUser);
        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevoPassword()));
        usuario.setRequiereCambioClave(false);
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepository.save(usuario);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Credenciales actualizadas exitosamente en PostgreSQL.");
        respuesta.put("nuevoUsername", usuario.getUsername());
        respuesta.put("requiereCambioClave", usuario.getRequiereCambioClave());
        respuesta.put("fechaActualizacion", usuario.getFechaActualizacion().toString());

        return ResponseEntity.ok(respuesta);
    }

    /**
     * Consulta el estado del usuario autenticado y si aún tiene pendiente cambiar la clave inicial del .env.
     */
    @GetMapping("/perfil")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> obtenerPerfil(Authentication authentication) {
        UsuarioSistema usuario = usuarioRepository.findByUsernameAndActivoTrue(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("username", usuario.getUsername());
        respuesta.put("rol", usuario.getRol());
        respuesta.put("requiereCambioClave", usuario.getRequiereCambioClave());
        respuesta.put("fechaActualizacion", usuario.getFechaActualizacion());

        return ResponseEntity.ok(respuesta);
    }
}