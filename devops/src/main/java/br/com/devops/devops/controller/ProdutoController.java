package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.multipart.MultipartFile;

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
    public String salvar(Produto produto,
                         @RequestParam(name = "fotoArquivo", required = false) MultipartFile fotoArquivo,
                         RedirectAttributes redirectAttributes) {
        try {
            produtoService.salvar(produto, fotoArquivo);
            redirectAttributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
            return "redirect:/produto/listar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/produto/formulario";
        }
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
    public String atualizar(@PathVariable Integer id, Produto produto,
                            @RequestParam(name = "fotoArquivo", required = false) MultipartFile fotoArquivo,
                            RedirectAttributes redirectAttributes) {
        try {
            produtoService.atualizar(id, produto, fotoArquivo);
            redirectAttributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
            return "redirect:/produto/listar";
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/produto/editar/" + id;
        }
    }

    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> foto(@PathVariable Integer id) {
        return produtoService.buscarPorId(id)
                .filter(produto -> produto.getFoto() != null && produto.getTipoFoto() != null)
                .map(produto -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(produto.getTipoFoto()))
                        .body(produto.getFoto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    //Metodo para excluir um produto por ID
    @GetMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        produtoService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Produto deletado com sucesso!");
        return "redirect:/produto/listar";
    }
}
