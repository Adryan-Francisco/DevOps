package br.com.devops.devops.service;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.entity.ItemDoPedido;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.entity.StatusPedido;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final AlunoRepository alunoRepository;

    public PedidoService(PedidoRepository pedidoRepository, AlunoRepository alunoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.alunoRepository = alunoRepository;
    }

    public List<Pedido> listarTodos() { return pedidoRepository.findAllByOrderByIdPedidoDesc(); }
    public Optional<Pedido> buscarPorId(Integer id) { return pedidoRepository.findById(id); }

    @Transactional
    public Pedido salvar(Pedido pedido, Integer alunoId) {
        vincularAluno(pedido, alunoId);
        if (pedido.getDataPedido() == null) pedido.setDataPedido(LocalDate.now());
        if (pedido.getStatus() == null) pedido.setStatus(StatusPedido.PENDENTE);
        pedido.setValorTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido atualizar(Integer id, Pedido dados, Integer alunoId) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Pedido não encontrado."));
        StatusPedido novoStatus = dados.getStatus() == null ? pedido.getStatus() : dados.getStatus();

        ajustarEstoquePorMudancaDeStatus(pedido, novoStatus);
        vincularAluno(pedido, alunoId);
        pedido.setDataPedido(dados.getDataPedido() == null ? pedido.getDataPedido() : dados.getDataPedido());
        pedido.setStatus(novoStatus);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public void deletar(Integer id) {
        pedidoRepository.findById(id).ifPresent(pedido -> {
            // Pedido cancelado já devolveu os itens ao estoque
            if (pedido.getStatus() != StatusPedido.CANCELADO) {
                pedido.getItens().forEach(this::devolverAoEstoque);
            }
            pedidoRepository.delete(pedido);
        });
    }

    private void ajustarEstoquePorMudancaDeStatus(Pedido pedido, StatusPedido novoStatus) {
        boolean estavaCancelado = pedido.getStatus() == StatusPedido.CANCELADO;
        boolean ficaraCancelado = novoStatus == StatusPedido.CANCELADO;

        if (!estavaCancelado && ficaraCancelado) {
            pedido.getItens().forEach(this::devolverAoEstoque);
        } else if (estavaCancelado && !ficaraCancelado) {
            pedido.getItens().forEach(item -> retirarDoEstoque(item.getProduto(), item.getQuantidade()));
        }
    }

    private void devolverAoEstoque(ItemDoPedido item) {
        Produto produto = item.getProduto();
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + item.getQuantidade());
    }

    static void retirarDoEstoque(Produto produto, int quantidade) {
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para \"" + produto.getNomeProduto()
                    + "\". Disponível: " + produto.getQuantidadeEstoque() + ".");
        }
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
    }

    private void vincularAluno(Pedido pedido, Integer alunoId) {
        if (alunoId == null) {
            throw new RegraNegocioException("Selecione um aluno para o pedido");
        }

        Aluno aluno = alunoRepository.findById(alunoId)
                .orElseThrow(() -> new RegraNegocioException("Aluno não encontrado"));
        pedido.setAluno(aluno);
        pedido.setNomeCliente(aluno.getNomeAluno());
    }
}
