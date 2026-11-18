package br.com.devops.devops.entity;

import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProdutoPrecoTest {

    private static ValidatorFactory validatorFactory;

    @BeforeAll
    static void iniciarValidador() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void fecharValidador() {
        validatorFactory.close();
    }

    @ParameterizedTest(name = "Aceita preço de {0}")
    @ValueSource(strings = {"0.01", "49.90"})
    void deveAceitarPrecoValido(String preco) {
        Produto produto = new Produto();
        produto.setPrecoProduto(new BigDecimal(preco));

        var violacoes = validatorFactory.getValidator().validateProperty(produto, "precoProduto");

        assertTrue(violacoes.isEmpty(), () -> "Violações inesperadas: " + violacoes);
    }

    @ParameterizedTest(name = "Rejeita preço de {0}")
    @ValueSource(strings = {"-0.01", "0.00", "0.009"})
    void deveRejeitarPrecoAbaixoDoMinimo(String preco) {
        Produto produto = new Produto();
        produto.setPrecoProduto(new BigDecimal(preco));

        var violacoes = validatorFactory.getValidator().validateProperty(produto, "precoProduto");

        assertEquals(1, violacoes.size());
        assertEquals("O preço deve ser maior que zero.", violacoes.iterator().next().getMessage());
    }

    @Test
    void deveRejeitarPrecoAusente() {
        Produto produto = new Produto();

        var violacoes = validatorFactory.getValidator().validateProperty(produto, "precoProduto");

        assertEquals(1, violacoes.size());
        assertEquals("O preço é obrigatório.", violacoes.iterator().next().getMessage());
    }
}
