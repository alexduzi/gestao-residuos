package br.com.fiap.gestaoresiduos.bdd.steps;

import br.com.fiap.gestaoresiduos.bdd.support.ApiCliente;
import br.com.fiap.gestaoresiduos.bdd.support.ContextoCenario;
import io.cucumber.java.pt.Quando;

public class RequisicaoSteps {

    private final ContextoCenario contexto;

    public RequisicaoSteps(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Quando("envio uma requisição {word} para {string}")
    public void enviarRequisicao(String metodo, String caminho) {
        contexto.setResposta(ApiCliente.enviar(metodo, contexto.resolver(caminho), contexto.getToken(), null));
    }

    @Quando("envio uma requisição {word} para {string} com o corpo:")
    public void enviarRequisicaoComCorpo(String metodo, String caminho, String corpo) {
        contexto.setResposta(ApiCliente.enviar(metodo, contexto.resolver(caminho), contexto.getToken(),
                contexto.resolver(corpo)));
    }
}
