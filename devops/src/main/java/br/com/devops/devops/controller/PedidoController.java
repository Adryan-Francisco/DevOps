package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "pedido/listarPedidos";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        Pedido pedido = new Pedido();
        pedido.setDataPedido(LocalDate.now());
        pedido.setStatus("PENDENTE");
        model.addAttribute("pedido", pedido);
        return "pedido/formularioPedido";
    }

    @PostMapping("/salvar")
    public String salvar(Pedido pedido, RedirectAttributes redirectAttributes) {
        Pedido salvo = pedidoService.salvar(pedido);
        redirectAttributes.addFlashAttribute("mensagem", "Pedido salvo. Agora adicione os itens.");
        return "redirect:/item-pedido/listar/" + salvo.getIdPedido();
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.buscarPorId(id).map(pedido -> {
            model.addAttribute("pedido", pedido);
            return "pedido/formularioPedido";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedido/listar";
        });
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, Pedido pedido, RedirectAttributes redirectAttributes) {
        pedidoService.atualizar(id, pedido);
        redirectAttributes.addFlashAttribute("mensagem", "Pedido atualizado com sucesso!");
        return "redirect:/pedido/listar";
    }

    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        pedidoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Pedido deletado com sucesso!");
        return "redirect:/pedido/listar";
    }
}
