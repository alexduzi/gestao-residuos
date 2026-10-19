package br.com.fiap.gestaoresiduos.bdd.support;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Estado compartilhado entre os steps de um mesmo cenario (token, ultima resposta e valores
 * guardados). Um novo objeto e criado para cada cenario, entao nada vaza entre cenarios.
 */
@ScenarioScope
public class ContextoCenario {

    private static final Pattern VARIAVEL = Pattern.compile("\\{(\\w+)}");

    private final Map<String, String> variaveis = new HashMap<>();
    private String token;
    private Response resposta;

    public ContextoCenario() {
        variaveis.put("unico", UUID.randomUUID().toString().substring(0, 8));
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Response getResposta() {
        if (resposta == null) {
            throw new IllegalStateException("Nenhuma requisicao foi enviada neste cenario.");
        }
        return resposta;
    }

    public void setResposta(Response resposta) {
        this.resposta = resposta;
    }

    public void guardar(String nome, Object valor) {
        variaveis.put(nome, String.valueOf(valor));
    }

    public String obter(String nome) {
        String valor = variaveis.get(nome);
        if (valor == null) {
            throw new IllegalStateException("Variavel nao definida no cenario: " + nome);
        }
        return valor;
    }

    /** Substitui {nome} pelos valores guardados (ex.: "/tipos-residuo/{idTipo}"). */
    public String resolver(String texto) {
        Matcher matcher = VARIAVEL.matcher(texto);
        StringBuilder resultado = new StringBuilder();
        while (matcher.find()) {
            String valor = variaveis.get(matcher.group(1));
            matcher.appendReplacement(resultado, Matcher.quoteReplacement(valor != null ? valor : matcher.group()));
        }
        matcher.appendTail(resultado);
        return resultado.toString();
    }
}
