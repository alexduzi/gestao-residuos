package br.com.fiap.gestaoresiduos.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.gestaoresiduos.bdd.support.ContextoCenario;
import br.com.fiap.gestaoresiduos.bdd.support.ValidadorContrato;
import io.cucumber.java.pt.Então;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;

public class RespostaSteps {

    private final ContextoCenario contexto;

    public RespostaSteps(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Então("o status code da resposta deve ser {int}")
    public void statusCode(int esperado) {
        Response resposta = contexto.getResposta();
        assertThat(resposta.statusCode())
                .as("status code (corpo: %s)", resposta.asString())
                .isEqualTo(esperado);
    }

    @Então("a resposta deve obedecer ao contrato {string}")
    public void obedeceContrato(String schema) {
        assertThat(ValidadorContrato.validar(contexto.getResposta().asString(), schema))
                .as("violacoes do contrato '%s'", schema)
                .isEmpty();
    }

    @Então("a resposta deve ser uma lista que obedece ao contrato {string}")
    public void listaObedeceContrato(String schema) {
        assertThat(ValidadorContrato.validarLista(contexto.getResposta().asString(), schema))
                .as("violacoes do contrato '%s'", schema)
                .isEmpty();
    }

    @Então("o campo {string} da resposta deve ser {string}")
    public void campoIgual(String campo, String esperado) {
        assertThat(contexto.getResposta().jsonPath().getString(campo))
                .as("campo '%s'", campo)
                .isEqualTo(contexto.resolver(esperado));
    }

    @Então("o campo {string} da resposta deve ser o número {bigdecimal}")
    public void campoNumerico(String campo, BigDecimal esperado) {
        String valor = contexto.getResposta().jsonPath().getString(campo);
        assertThat(valor).as("campo '%s'", campo).isNotNull();
        assertThat(new BigDecimal(valor)).as("campo '%s'", campo).isEqualByComparingTo(esperado);
    }

    @Então("o campo {string} da resposta não deve estar vazio")
    public void campoPreenchido(String campo) {
        assertThat(contexto.getResposta().jsonPath().getString(campo))
                .as("campo '%s'", campo)
                .isNotBlank();
    }

    @Então("a lista da resposta deve conter {int} item/itens")
    public void tamanhoLista(int quantidade) {
        assertThat(contexto.getResposta().jsonPath().getList("$")).hasSize(quantidade);
    }

    @Então("a lista da resposta não deve estar vazia")
    public void listaNaoVazia() {
        assertThat(contexto.getResposta().jsonPath().getList("$")).isNotEmpty();
    }

    @Então("todos os itens da lista devem ter o campo {string} igual a {string}")
    public void todosItensComCampo(String campo, String esperado) {
        List<String> valores = contexto.getResposta().jsonPath().getList(campo, String.class);
        assertThat(valores).as("valores do campo '%s'", campo).isNotEmpty().containsOnly(contexto.resolver(esperado));
    }

    @Então("a lista de erros de validação deve conter o campo {string}")
    public void erroDeValidacaoNoCampo(String campo) {
        assertThat(contexto.getResposta().jsonPath().getList("errors.fieldName", String.class)).contains(campo);
    }

    @Então("o cabeçalho {string} da resposta deve terminar com {string}")
    public void cabecalhoTerminaCom(String cabecalho, String sufixo) {
        assertThat(contexto.getResposta().header(cabecalho))
                .as("cabecalho '%s'", cabecalho)
                .endsWith(contexto.resolver(sufixo));
    }

    @Então("a resposta não deve ter corpo")
    public void semCorpo() {
        assertThat(contexto.getResposta().asString()).isEmpty();
    }

    @Então("guardo o campo {string} da resposta como {string}")
    public void guardarCampo(String campo, String nome) {
        String valor = contexto.getResposta().jsonPath().getString(campo);
        assertThat(valor).as("campo '%s' a ser guardado", campo).isNotNull();
        contexto.guardar(nome, valor);
    }
}
