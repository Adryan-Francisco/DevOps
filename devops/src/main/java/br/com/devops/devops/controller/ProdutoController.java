package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    private static final String FORMULARIO = "produto/formularioProduto";

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
        return FORMULARIO;
    }

    // Busca um produto para ser editado por ID
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id).map(produto -> {
            model.addAttribute("produto", produto);
            return FORMULARIO;
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
            return "redirect:/produto/listar";
        });
    }

    // Salva o cadastro do produto
    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Produto produto, BindingResult bindingResult,
                         @RequestParam(name = "fotoArquivo", required = false) MultipartFile fotoArquivo,
                         Model model, RedirectAttributes redirectAttributes) {
        produto.setIdProduto(null);
        if (bindingResult.hasErrors()) {
            return FORMULARIO;
        }
        try {
            produtoService.salvar(produto, fotoArquivo);
            redirectAttributes.addFlashAttribute("mensagem", "Produto cadastrado com sucesso!");
            return "redirect:/produto/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return FORMULARIO;
        }
    }

    // Atualiza um produto por ID
    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @Valid @ModelAttribute Produto produto,
                            BindingResult bindingResult,
                            @RequestParam(name = "fotoArquivo", required = false) MultipartFile fotoArquivo,
                            Model model, RedirectAttributes redirectAttributes) {
        produto.setIdProduto(id);
        if (bindingResult.hasErrors()) {
            manterFotoAtual(produto);
            return FORMULARIO;
        }
        try {
            produtoService.atualizar(id, produto, fotoArquivo);
            redirectAttributes.addFlashAttribute("mensagem", "Produto atualizado com sucesso!");
            return "redirect:/produto/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            manterFotoAtual(produto);
            return FORMULARIO;
        }
    }

    @GetMapping("/foto/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> foto(@PathVariable Integer id) {
        return produtoService.buscarPorId(id)
                .filter(produto -> produto.getFoto() != null && produto.getTipoFoto() != null)
                .map(produto -> ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(produto.getTipoFoto()))
                        .cacheControl(CacheControl.noCache())
                        .body(produto.getFoto()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Exclui um produto por ID
    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Produto excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/produto/listar";
    }

    // Ao reexibir o formulário com erro, mostra a foto que já está salva
    private void manterFotoAtual(Produto produto) {
        produtoService.buscarPorId(produto.getIdProduto()).ifPresent(salvo -> {
            produto.setFoto(salvo.getFoto());
            produto.setTipoFoto(salvo.getTipoFoto());
        });
    }
}
