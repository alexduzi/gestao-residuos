package br.com.fiap.gestaoresiduos.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.fiap.gestaoresiduos.domain.Usuario;
import br.com.fiap.gestaoresiduos.dto.request.LoginRequest;
import br.com.fiap.gestaoresiduos.dto.request.RegisterRequest;
import br.com.fiap.gestaoresiduos.exception.BusinessException;
import br.com.fiap.gestaoresiduos.exception.ForbiddenException;
import br.com.fiap.gestaoresiduos.exception.ResourceNotFoundException;
import br.com.fiap.gestaoresiduos.infra.security.JwtService;
import br.com.fiap.gestaoresiduos.repository.UsuarioRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(usuarioRepository, passwordEncoder, jwtService);
    }

    private Usuario usuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Ana Teste");
        usuario.setEmail("ana@test.com");
        usuario.setSenhaHash("hash-armazenado");
        usuario.setRole("USER");
        return usuario;
    }

    @Test
    void registerLancaBusinessExceptionQuandoEmailJaCadastrado() {
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(usuarioExistente()));

        assertThatThrownBy(() -> authService.register(new RegisterRequest("Ana", "ana@test.com", "senha123")))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void registerCriaUsuarioComRoleUserERetornaTokenGerado() {
        when(usuarioRepository.findByEmail("nova@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha123")).thenReturn("hash-gerado");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtService.generateToken(any(Usuario.class))).thenReturn("token-gerado");

        var response = authService.register(new RegisterRequest("Nova Usuaria", "nova@test.com", "senha123"));

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo("USER");
        assertThat(captor.getValue().getSenhaHash()).isEqualTo("hash-gerado");
        assertThat(response.token()).isEqualTo("token-gerado");
        assertThat(response.email()).isEqualTo("nova@test.com");
    }

    @Test
    void loginLancaResourceNotFoundExceptionQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findByEmail("inexistente@test.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(new LoginRequest("inexistente@test.com", "qualquer")))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void loginLancaForbiddenExceptionQuandoSenhaEstaIncorreta() {
        Usuario usuario = usuarioExistente();
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-errada", "hash-armazenado")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(new LoginRequest("ana@test.com", "senha-errada")))
                .isInstanceOf(ForbiddenException.class);
    }

    @Test
    void loginRetornaTokenQuandoCredenciaisEstaoCorretas() {
        Usuario usuario = usuarioExistente();
        when(usuarioRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-correta", "hash-armazenado")).thenReturn(true);
        when(jwtService.generateToken(usuario)).thenReturn("token-valido");

        var response = authService.login(new LoginRequest("ana@test.com", "senha-correta"));

        assertThat(response.token()).isEqualTo("token-valido");
        assertThat(response.role()).isEqualTo("USER");
    }
}
