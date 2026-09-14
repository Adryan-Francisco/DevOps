package br.com.devops.devops.repository;

import br.com.devops.devops.entity.ItemDoPedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemDoPedidoRepository extends JpaRepository<ItemDoPedido, Integer> {

    boolean existsByProdutoIdProduto(Integer idProduto);
}
