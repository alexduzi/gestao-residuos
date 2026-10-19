package br.com.fiap.gestaoresiduos.bdd.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Testes de contrato: valida o JSON de resposta contra os JSON Schemas (draft-07) de
 * src/test/resources/schemas.
 */
public final class ValidadorContrato {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonSchemaFactory FACTORY = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);

    private ValidadorContrato() {
    }

    /** Retorna as violacoes de contrato; vazio quando a resposta respeita o schema. */
    public static Set<String> validar(String json, String nomeSchema) {
        JsonSchema schema = carregar(nomeSchema);
        try {
            Set<ValidationMessage> erros = schema.validate(MAPPER.readTree(json));
            return erros.stream().map(ValidationMessage::getMessage).collect(Collectors.toSet());
        } catch (IOException ex) {
            throw new IllegalStateException("Resposta nao e um JSON valido: " + json, ex);
        }
    }

    /** Valida cada item de uma resposta em lista contra o schema do item. */
    public static Set<String> validarLista(String json, String nomeSchemaItem) {
        try {
            JsonNode raiz = MAPPER.readTree(json);
            if (!raiz.isArray()) {
                return Set.of("a resposta deveria ser uma lista JSON, mas foi: " + raiz.getNodeType());
            }
            JsonSchema schema = carregar(nomeSchemaItem);
            Set<String> erros = new TreeSet<>();
            for (int i = 0; i < raiz.size(); i++) {
                for (ValidationMessage erro : schema.validate(raiz.get(i))) {
                    erros.add("[" + i + "] " + erro.getMessage());
                }
            }
            return erros;
        } catch (IOException ex) {
            throw new IllegalStateException("Resposta nao e um JSON valido: " + json, ex);
        }
    }

    private static JsonSchema carregar(String nomeSchema) {
        String caminho = "/schemas/" + nomeSchema + ".json";
        InputStream arquivo = ValidadorContrato.class.getResourceAsStream(caminho);
        if (arquivo == null) {
            throw new IllegalArgumentException("Schema nao encontrado no classpath: " + caminho);
        }
        return FACTORY.getSchema(arquivo);
    }
}
