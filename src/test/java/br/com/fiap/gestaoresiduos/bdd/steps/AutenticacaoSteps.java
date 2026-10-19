package br.com.fiap.gestaoresiduos.bdd.steps;

import br.com.fiap.gestaoresiduos.bdd.support.ApiCliente;
import br.com.fiap.gestaoresiduos.bdd.support.ContextoCenario;
import io.cucumber.java.pt.Dado;

public class AutenticacaoSteps {

    private final ContextoCenario contexto;

    public AutenticacaoSteps(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que estou autenticado como administrador")
    public void autenticadoComoAdministrador() {
        contexto.setToken(ApiCliente.login(ApiCliente.ADMIN_EMAIL, ApiCliente.ADMIN_SENHA));
    }

    @Dado("que estou autenticado como usuário comum")
    public void autenticadoComoUsuarioComum() {
        contexto.setToken(ApiCliente.login(ApiCliente.USER_EMAIL, ApiCliente.USER_SENHA));
    }

    @Dado("que não estou autenticado")
    public void naoAutenticado() {
        contexto.setToken(null);
    }
}
