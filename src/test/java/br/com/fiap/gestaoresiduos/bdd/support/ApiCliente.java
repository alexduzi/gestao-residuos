package br.com.fiap.gestaoresiduos.bdd.support;

import static io.restassured.RestAssured.given;

import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

/** Chamadas HTTP a API via RestAssured (porta e basePath configurados em Hooks). */
public final class ApiCliente {

    public static final String ADMIN_EMAIL = "admin@esg.com";
    public static final String ADMIN_SENHA = "admin123";
    public static final String USER_EMAIL = "user@esg.com";
    public static final String USER_SENHA = "user123";

    private ApiCliente() {
    }

    public static Response enviar(String metodo, String caminho, String token, Object corpo) {
        RequestSpecification requisicao = given().accept(ContentType.JSON);
        if (token != null) {
            requisicao.header("Authorization", "Bearer " + token);
        }
        if (corpo != null) {
            requisicao.contentType(ContentType.JSON).body(corpo);
        }
        return requisicao.request(Method.valueOf(metodo.toUpperCase()), caminho);
    }

    public static String login(String email, String senha) {
        Response resposta = enviar("POST", "/auth/login", null, Map.of("email", email, "senha", senha));
        if (resposta.statusCode() != 200) {
            throw new IllegalStateException("Falha no login de " + email + ": " + resposta.asString());
        }
        return resposta.jsonPath().getString("token");
    }

    public static String tokenAdmin() {
        return login(ADMIN_EMAIL, ADMIN_SENHA);
    }
}
