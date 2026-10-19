package br.com.fiap.gestaoresiduos.bdd;

import br.com.fiap.gestaoresiduos.bdd.support.ContextoCenario;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("bdd")
@Import(ContextoCenario.class)
public class CucumberSpringConfiguration {
}
