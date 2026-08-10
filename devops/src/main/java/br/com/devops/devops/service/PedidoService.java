package br.com.devops.devops.service;

import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {
    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> listarTodos() { return pedidoRepository.findAll(); }
    public Optional<Pedido> buscarPorId(Integer id) { return pedidoRepository.findById(id); }
    public void deletar(Integer id) { pedidoRepository.deleteById(id); }

    public Pedido salvar(Pedido pedido) {
        if (pedido.getDataPedido() == null) pedido.setDataPedido(LocalDate.now());
        if (pedido.getStatus() == null || pedido.getStatus().isBlank()) pedido.setStatus("PENDENTE");
        pedido.setValorTotal(BigDecimal.ZERO);
        return pedidoRepository.save(pedido);
    }

    public Pedido atualizar(Integer id, Pedido dados) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado"));
        pedido.setNomeCliente(dados.getNomeCliente());
        pedido.setDataPedido(dados.getDataPedido());
        pedido.setStatus(dados.getStatus());
        return pedidoRepository.save(pedido);
    }
}
