package br.com.devops.devops.controller;

import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/item-pedido")
public class ItemDoPedidoController {
    private final ItemDoPedidoService itemService;
    private final PedidoService pedidoService;
    private final ProdutoService produtoService;

    public ItemDoPedidoController(ItemDoPedidoService itemService, PedidoService pedidoService,
                                  ProdutoService produtoService) {
        this.itemService = itemService;
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
    }

    @GetMapping("/listar/{pedidoId}")
    public String listar(@PathVariable Integer pedidoId, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.buscarPorId(pedidoId).map(pedido -> {
            model.addAttribute("pedido", pedido);
            model.addAttribute("produtos", produtoService.listarTodos());
            return "itemPedido/listarItens";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
            return "redirect:/pedido/listar";
        });
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam Integer pedidoId, @RequestParam(required = false) Integer produtoId,
                         @RequestParam(required = false) Integer quantidade, RedirectAttributes redirectAttributes) {
        try {
            itemService.adicionar(pedidoId, produtoId, quantidade);
            redirectAttributes.addFlashAttribute("mensagem", "Item adicionado ao pedido.");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/item-pedido/listar/" + pedidoId;
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @RequestParam Integer pedidoId,
                            @RequestParam(required = false) Integer quantidade,
                            RedirectAttributes redirectAttributes) {
        try {
            itemService.atualizarQuantidade(id, quantidade);
            redirectAttributes.addFlashAttribute("mensagem", "Quantidade atualizada.");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/item-pedido/listar/" + pedidoId;
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, @RequestParam Integer pedidoId,
                          RedirectAttributes redirectAttributes) {
        try {
            itemService.remover(id);
            redirectAttributes.addFlashAttribute("mensagem", "Item removido do pedido.");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/item-pedido/listar/" + pedidoId;
    }
}
