package br.com.devops.devops.repository;

import br.com.devops.devops.entity.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    @EntityGraph(attributePaths = {"aluno", "itens", "itens.produto"})
    List<Pedido> findAllByOrderByIdPedidoDesc();

    @Override
    @EntityGraph(attributePaths = {"aluno", "itens", "itens.produto"})
    Optional<Pedido> findById(Integer id);

    @EntityGraph(attributePaths = {"aluno"})
    List<Pedido> findTop5ByOrderByIdPedidoDesc();

    boolean existsByAlunoIdAluno(Integer idAluno);
}
