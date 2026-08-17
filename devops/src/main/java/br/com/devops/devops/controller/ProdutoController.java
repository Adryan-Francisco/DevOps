package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("produtos", produtoService.listarTodos());
        return "produto/listarProdutos";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("produto", new Produto());
        return "produto/formularioProduto";
    }

    //Salva o cadastro do produto
    @PostMapping("/salvar")
    public String salvar(Produto produto, RedirectAttributes redirectAttributes) {
        produtoService.salvar(produto);
        redirectAttributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
        return "redirect:/produto/listar";
    }
    
    //Busca um produto para ser editado por ID
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id).map(produto -> {
            model.addAttribute("produto", produto);
            return "produto/formularioProduto";
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/produto/listar";
        });
    }
    //Atualiza um produto por ID
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, Produto produto, RedirectAttributes redirectAttributes) {
        produtoService.atualizar(id, produto);
        redirectAttributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
        return "redirect:/produto/listar";
    }

    //Metodo para excluir um produto por ID
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        produtoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Produto deletado com sucesso!");
        return "redirect:/produto/listar";
    }
}
