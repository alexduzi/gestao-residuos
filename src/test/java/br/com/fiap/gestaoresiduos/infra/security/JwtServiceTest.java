package br.com.fiap.gestaoresiduos.infra.security;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.gestaoresiduos.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "ZXNnLXJlc2lkdW9zLWFwaS1zZWNyZXQta2V5LWZvci1hY2FkZW1pYy1wdXJwb3Nlcy0yMDI1";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3600L);
    }

    private Usuario usuarioComEmail(String email) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1L);
        usuario.setNome("Usuario Teste");
        usuario.setEmail(email);
        usuario.setSenhaHash("hash");
        usuario.setRole("USER");
        return usuario;
    }

    @Test
    void generateTokenEExtractUsernameRetornamOMesmoEmail() {
        Usuario usuario = usuarioComEmail("user@test.com");

        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.extractUsername(token)).isEqualTo("user@test.com");
    }

    @Test
    void isTokenValidRetornaTrueParaTokenDoMesmoUsuario() {
        Usuario usuario = usuarioComEmail("user@test.com");
        String token = jwtService.generateToken(usuario);

        assertThat(jwtService.isTokenValid(token, usuario)).isTrue();
    }

    @Test
    void isTokenValidRetornaFalseQuandoTokenPertenceAOutroUsuario() {
        Usuario titular = usuarioComEmail("titular@test.com");
        Usuario outro = usuarioComEmail("outro@test.com");
        String token = jwtService.generateToken(titular);

        assertThat(jwtService.isTokenValid(token, outro)).isFalse();
    }

    @Test
    void isTokenValidRetornaFalseParaTokenMalformado() {
        Usuario usuario = usuarioComEmail("user@test.com");

        assertThat(jwtService.isTokenValid("token-completamente-invalido", usuario)).isFalse();
    }
}
