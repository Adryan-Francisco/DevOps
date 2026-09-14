package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.StatusPedido;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.AlunoService;
import br.com.devops.devops.service.PedidoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/pedido")
public class PedidoController {
    private static final String FORMULARIO = "pedido/formularioPedido";

    private final PedidoService pedidoService;
    private final AlunoService alunoService;

    public PedidoController(PedidoService pedidoService, AlunoService alunoService) {
        this.pedidoService = pedidoService;
        this.alunoService = alunoService;
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
        pedido.setStatus(StatusPedido.PENDENTE);
        return exibirFormulario(pedido, null, model);
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Pedido pedido, @RequestParam(required = false) Integer alunoId,
                         Model model, RedirectAttributes redirectAttributes) {
        pedido.setIdPedido(null);
        try {
            Pedido salvo = pedidoService.salvar(pedido, alunoId);
            redirectAttributes.addFlashAttribute("mensagem", "Pedido criado. Agora adicione os itens.");
            return "redirect:/item-pedido/listar/" + salvo.getIdPedido();
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return exibirFormulario(pedido, alunoId, model);
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return pedidoService.buscarPorId(id)
                .map(pedido -> exibirFormulario(pedido, pedido.getAluno() != null ? pedido.getAluno().getIdAluno() : null, model))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Pedido não encontrado.");
                    return "redirect:/pedido/listar";
                });
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @ModelAttribute Pedido pedido,
                            @RequestParam(required = false) Integer alunoId,
                            Model model, RedirectAttributes redirectAttributes) {
        pedido.setIdPedido(id);
        try {
            pedidoService.atualizar(id, pedido, alunoId);
            redirectAttributes.addFlashAttribute("mensagem", "Pedido atualizado com sucesso!");
            return "redirect:/pedido/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return exibirFormulario(pedido, alunoId, model);
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        pedidoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Pedido excluído com sucesso!");
        return "redirect:/pedido/listar";
    }

    private String exibirFormulario(Pedido pedido, Integer alunoId, Model model) {
        model.addAttribute("pedido", pedido);
        model.addAttribute("alunoId", alunoId);
        model.addAttribute("alunos", alunoService.listarTodos());
        model.addAttribute("statusOpcoes", StatusPedido.values());
        return FORMULARIO;
    }
}
