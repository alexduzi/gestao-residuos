package br.com.fiap.gestaoresiduos.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.fiap.gestaoresiduos.bdd.support.ApiCliente;
import br.com.fiap.gestaoresiduos.bdd.support.ContextoCenario;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Steps em linguagem de negocio dos cenarios ESG (alerta de capacidade, registro de coleta e
 * controle de acesso). A preparacao de dados ("Dado que existe...") e as verificacoes
 * colaterais usam o token de administrador, sem alterar o token nem a resposta do cenario.
 */
public class RegrasNegocioSteps {

    private final ContextoCenario contexto;

    public RegrasNegocioSteps(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que existe um ponto de coleta de {string} com capacidade máxima de {bigdecimal} kg e volume atual de {bigdecimal} kg")
    public void existePontoDeColetaDoTipo(String descricaoTipo, BigDecimal capacidade, BigDecimal volume) {
        List<Map<String, Object>> tipos = consultarComoAdmin("/tipos-residuo").getList("findAll { it.descricao == '" + descricaoTipo + "' }");
        assertThat(tipos).as("tipo de residuo '%s' nos dados de teste", descricaoTipo).isNotEmpty();
        contexto.guardar("idTipo", tipos.get(0).get("idTipo"));
        existePontoDeColetaDesseTipo(capacidade, volume);
    }

    @Dado("que existe um tipo de resíduo reciclável {string}")
    public void existeTipoDeResiduoReciclavel(String descricao) {
        Response resposta = ApiCliente.enviar("POST", "/tipos-residuo", ApiCliente.tokenAdmin(),
                Map.of("descricao", descricao, "reciclavel", "S", "perigoso", "N"));
        assertThat(resposta.statusCode()).as("criacao do tipo de residuo").isEqualTo(201);
        contexto.guardar("idTipo", resposta.jsonPath().getLong("idTipo"));
    }

    @Dado("que existe um ponto de coleta desse tipo com capacidade máxima de {bigdecimal} kg e volume atual de {bigdecimal} kg")
    public void existePontoDeColetaDesseTipo(BigDecimal capacidade, BigDecimal volume) {
        Response resposta = ApiCliente.enviar("POST", "/pontos-coleta", ApiCliente.tokenAdmin(), Map.of(
                "endereco", "Rua dos Testes BDD, " + contexto.obter("unico") + " - Sao Paulo/SP",
                "idTipo", Long.valueOf(contexto.obter("idTipo")),
                "capacidadeMaxKg", capacidade,
                "volumeAtualKg", volume));
        assertThat(resposta.statusCode()).as("criacao do ponto de coleta").isEqualTo(201);
        contexto.guardar("idPonto", resposta.jsonPath().getLong("idPonto"));
    }

    @Dado("que o volume do ponto foi atualizado para {bigdecimal} kg, gerando um alerta pendente")
    public void volumeAtualizadoGerandoAlerta(BigDecimal volume) {
        Response resposta = ApiCliente.enviar("PATCH", "/pontos-coleta/" + contexto.obter("idPonto") + "/volume",
                ApiCliente.tokenAdmin(), Map.of("volumeAtualKg", volume));
        assertThat(resposta.statusCode()).as("atualizacao de volume").isEqualTo(200);
        assertThat(alertasDoPonto("N")).as("alertas pendentes apos atualizar o volume").hasSize(1);
    }

    @Quando("o volume do ponto de coleta for atualizado para {bigdecimal} kg")
    public void atualizarVolume(BigDecimal volume) {
        contexto.setResposta(ApiCliente.enviar("PATCH", "/pontos-coleta/" + contexto.obter("idPonto") + "/volume",
                contexto.getToken(), Map.of("volumeAtualKg", volume)));
    }

    @Quando("eu registrar uma coleta de {bigdecimal} kg no ponto em {string} pela empresa {string}")
    public void registrarColeta(BigDecimal peso, String data, String empresa) {
        registrarColetaNoPonto(peso, Long.valueOf(contexto.obter("idPonto")), data, empresa);
    }

    @Quando("eu registrar uma coleta de {bigdecimal} kg no ponto de id {long} em {string} pela empresa {string}")
    public void registrarColetaNoPonto(BigDecimal peso, Long idPonto, String data, String empresa) {
        contexto.setResposta(ApiCliente.enviar("POST", "/coletas", contexto.getToken(), Map.of(
                "idPonto", idPonto,
                "dataColeta", data,
                "pesoRecolhidoKg", peso,
                "empresaResponsavel", empresa)));
    }

    @Então("deve existir {int} alerta(s) pendente(s) para o ponto de coleta com a mensagem {string}")
    public void alertasPendentesComMensagem(int quantidade, String mensagem) {
        List<Map<String, Object>> pendentes = alertasDoPonto("N");
        assertThat(pendentes).hasSize(quantidade);
        assertThat(pendentes).extracting(alerta -> alerta.get("mensagem")).containsOnly(mensagem);
    }

    @Então("não deve existir alerta para o ponto de coleta")
    public void nenhumAlerta() {
        assertThat(alertasDoPonto(null)).isEmpty();
    }

    @Então("não deve existir alerta pendente para o ponto de coleta")
    public void nenhumAlertaPendente() {
        assertThat(alertasDoPonto("N")).isEmpty();
        assertThat(alertasDoPonto("S")).as("alertas resolvidos pela coleta").isNotEmpty();
    }

    @Então("o volume atual do ponto de coleta deve ser {bigdecimal} kg")
    public void volumeAtual(BigDecimal esperado) {
        BigDecimal volume = new BigDecimal(consultarComoAdmin("/pontos-coleta/" + contexto.obter("idPonto"))
                .getString("volumeAtualKg"));
        assertThat(volume).isEqualByComparingTo(esperado);
    }

    @Então("o consolidado de reciclagem do tipo em {string} deve totalizar {bigdecimal} kg")
    public void consolidadoDoTipo(String mesAno, BigDecimal total) {
        List<Map<String, Object>> consolidados = consultarComoAdmin("/consolidado/tipo/" + contexto.obter("idTipo"))
                .getList("findAll { it.mesAno == '" + mesAno + "' }");
        assertThat(consolidados).as("consolidado do tipo em %s", mesAno).hasSize(1);
        assertThat(new BigDecimal(String.valueOf(consolidados.get(0).get("totalRecicladoKg"))))
                .isEqualByComparingTo(total);
    }

    @Então("não deve existir coleta registrada para o ponto de coleta")
    public void nenhumaColeta() {
        assertThat(consultarComoAdmin("/coletas/ponto/" + contexto.obter("idPonto")).getList("$")).isEmpty();
    }

    private List<Map<String, Object>> alertasDoPonto(String statusResolvido) {
        JsonPath alertas = consultarComoAdmin("/alertas/ponto/" + contexto.obter("idPonto"));
        return statusResolvido == null
                ? alertas.getList("$")
                : alertas.getList("findAll { it.statusResolvido == '" + statusResolvido + "' }");
    }

    private JsonPath consultarComoAdmin(String caminho) {
        Response resposta = ApiCliente.enviar("GET", caminho, ApiCliente.tokenAdmin(), null);
        assertThat(resposta.statusCode()).as("GET %s", caminho).isEqualTo(200);
        return resposta.jsonPath();
    }
}
