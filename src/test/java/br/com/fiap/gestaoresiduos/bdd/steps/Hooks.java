package br.com.fiap.gestaoresiduos.bdd.steps;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import org.springframework.beans.factory.annotation.Value;

public class Hooks {

    @Value("${local.server.port}")
    private int porta;

    @Before(order = 0)
    public void configurarRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = porta;
        RestAssured.basePath = "/api/v1";
    }
}
