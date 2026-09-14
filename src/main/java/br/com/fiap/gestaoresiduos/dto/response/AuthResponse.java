package br.com.fiap.gestaoresiduos.dto.response;

public record AuthResponse(
        String token,
        String email,
        String role
) {}
