package br.com.devops.devops.marketplace;

import java.math.BigDecimal;

import br.com.devops.devops.entity.Produto;

public record ItemCarrinho(Produto produto, int quantidade, BigDecimal subtotal) {

    public static ItemCarrinho de(Produto produto, int quantidade) {
        BigDecimal subtotal = produto.getPrecoProduto().multiply(BigDecimal.valueOf(quantidade));
        return new ItemCarrinho(produto, quantidade, subtotal);
    }
}
