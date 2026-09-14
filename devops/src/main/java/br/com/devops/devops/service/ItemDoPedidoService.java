package br.com.devops.devops.service;

import br.com.devops.devops.entity.ItemDoPedido;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.ItemDoPedidoRepository;
import br.com.devops.devops.repository.PedidoRepository;
import br.com.devops.devops.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ItemDoPedidoService {
    private final ItemDoPedidoRepository itemRepository;
    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public ItemDoPedidoService(ItemDoPedidoRepository itemRepository, PedidoRepository pedidoRepository,
                               ProdutoRepository produtoRepository) {
        this.itemRepository = itemRepository;
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    // Adiciona um produto ao pedido; se o produto já estiver no pedido, soma a quantidade
    public void adicionar(Integer pedidoId, Integer produtoId, Integer quantidade) {
        validarQuantidade(quantidade);
        Pedido pedido = buscarPedidoEditavel(pedidoId);
        if (produtoId == null) {
            throw new RegraNegocioException("Selecione um produto.");
        }
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));

        PedidoService.retirarDoEstoque(produto, quantidade);

        ItemDoPedido item = pedido.getItens().stream()
                .filter(existente -> existente.getProduto().getIdProduto().equals(produtoId))
                .findFirst()
                .orElseGet(() -> novoItem(pedido, produto));
        item.setQuantidade(item.getQuantidade() + quantidade);
        item.atualizarSubtotal();

        pedido.recalcularTotal();
        pedidoRepository.save(pedido);
    }

    // Altera a quantidade de um item e retorna o id do pedido
    public Integer atualizarQuantidade(Integer itemId, Integer quantidade) {
        validarQuantidade(quantidade);
        ItemDoPedido item = buscarItem(itemId);
        Pedido pedido = buscarPedidoEditavel(item.getPedido().getIdPedido());

        int diferenca = quantidade - item.getQuantidade();
        if (diferenca > 0) {
            PedidoService.retirarDoEstoque(item.getProduto(), diferenca);
        } else {
            Produto produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - diferenca);
        }

        item.setQuantidade(quantidade);
        item.atualizarSubtotal();
        pedido.recalcularTotal();
        pedidoRepository.save(pedido);
        return pedido.getIdPedido();
    }

    // Remove o item, devolve a quantidade ao estoque e retorna o id do pedido
    public Integer remover(Integer itemId) {
        ItemDoPedido item = buscarItem(itemId);
        Pedido pedido = buscarPedidoEditavel(item.getPedido().getIdPedido());

        Produto produto = item.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());

        pedido.getItens().remove(item);
        pedido.recalcularTotal();
        pedidoRepository.save(pedido);
        return pedido.getIdPedido();
    }

    private ItemDoPedido novoItem(Pedido pedido, Produto produto) {
        ItemDoPedido item = new ItemDoPedido();
        item.setPedido(pedido);
        item.setProduto(produto);
        item.setQuantidade(0);
        item.setPrecoUnitario(produto.getPrecoProduto());
        pedido.getItens().add(item);
        return item;
    }

    private ItemDoPedido buscarItem(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RegraNegocioException("Item não encontrado."));
    }

    private Pedido buscarPedidoEditavel(Integer pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));
        if (!pedido.getStatus().permiteAlterarItens()) {
            throw new RegraNegocioException("Pedidos " + pedido.getStatus().getDescricao().toLowerCase()
                    + "s não podem ter os itens alterados.");
        }
        return pedido;
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade < 1) {
            throw new RegraNegocioException("A quantidade deve ser maior que zero.");
        }
    }
}
