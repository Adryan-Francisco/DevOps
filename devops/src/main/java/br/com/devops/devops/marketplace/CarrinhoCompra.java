package br.com.devops.devops.marketplace;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import br.com.devops.devops.exception.RegraNegocioException;

@Component
@SessionScope
public class CarrinhoCompra implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, Integer> quantidades = new LinkedHashMap<>();

    public void adicionar(Integer produtoId, int quantidade, int estoqueDisponivel) {
        validarQuantidade(quantidade);
        int novaQuantidade = quantidades.getOrDefault(produtoId, 0) + quantidade;
        validarEstoque(novaQuantidade, estoqueDisponivel);
        quantidades.put(produtoId, novaQuantidade);
    }

    public void atualizar(Integer produtoId, int quantidade, int estoqueDisponivel) {
        validarQuantidade(quantidade);
        validarEstoque(quantidade, estoqueDisponivel);
        if (!quantidades.containsKey(produtoId)) {
            throw new RegraNegocioException("Produto não encontrado no carrinho.");
        }
        quantidades.put(produtoId, quantidade);
    }

    public void remover(Integer produtoId) {
        quantidades.remove(produtoId);
    }

    public void limpar() {
        quantidades.clear();
    }

    public int getQuantidadeTotal() {
        return quantidades.values().stream().mapToInt(Integer::intValue).sum();
    }

    public Map<Integer, Integer> getQuantidades() {
        return Collections.unmodifiableMap(quantidades);
    }

    private void validarQuantidade(int quantidade) {
        if (quantidade < 1) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }

    private void validarEstoque(int quantidade, int estoqueDisponivel) {
        if (quantidade > estoqueDisponivel) {
            throw new RegraNegocioException("Quantidade indisponível em estoque.");
        }
    }
}
