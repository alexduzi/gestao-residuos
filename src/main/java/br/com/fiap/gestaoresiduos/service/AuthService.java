package br.com.fiap.gestaoresiduos.service;

import br.com.fiap.gestaoresiduos.domain.Usuario;
import br.com.fiap.gestaoresiduos.dto.request.LoginRequest;
import br.com.fiap.gestaoresiduos.dto.request.RegisterRequest;
import br.com.fiap.gestaoresiduos.dto.response.AuthResponse;
import br.com.fiap.gestaoresiduos.exception.BusinessException;
import br.com.fiap.gestaoresiduos.exception.ForbiddenException;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.infra.security.JwtService;
import br.com.fiap.gestaoresiduos.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("Email ja cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenhaHash(passwordEncoder.encode(dto.senha()));
        usuario.setRole("USER");
        usuario = usuarioRepository.save(usuario);

        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRole());
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado: " + dto.email()));
        if (!passwordEncoder.matches(dto.senha(), usuario.getSenhaHash())) {
            throw new ForbiddenException("Credenciais invalidas.");
        }
        String token = jwtService.generateToken(usuario);
        return new AuthResponse(token, usuario.getEmail(), usuario.getRole());
    }
}
