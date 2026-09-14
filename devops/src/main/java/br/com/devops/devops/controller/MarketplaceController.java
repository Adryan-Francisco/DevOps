package br.com.devops.devops.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.marketplace.CarrinhoCompra;
import br.com.devops.devops.marketplace.ItemCarrinho;
import br.com.devops.devops.service.ProdutoService;

@Controller
public class MarketplaceController {
    private final ProdutoService produtoService;
    private final CarrinhoCompra carrinho;

    public MarketplaceController(ProdutoService produtoService, CarrinhoCompra carrinho) {
        this.produtoService = produtoService;
        this.carrinho = carrinho;
    }

    @GetMapping("/loja")
    public String loja(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", produtoService.listarDisponiveis(busca));
        model.addAttribute("busca", busca == null ? "" : busca.trim());
        adicionarResumoCarrinho(model);
        return "marketplace/loja";
    }

    @GetMapping("/carrinho")
    public String carrinho(Model model) {
        adicionarResumoCarrinho(model);
        return "marketplace/carrinho";
    }

    @PostMapping("/carrinho/adicionar/{produtoId}")
    public String adicionar(@PathVariable Integer produtoId,
            @RequestParam(defaultValue = "1") Integer quantidade,
            RedirectAttributes redirectAttributes) {
        try {
            Produto produto = buscarProduto(produtoId);
            carrinho.adicionar(produtoId, quantidade == null ? 0 : quantidade, produto.getQuantidadeEstoque());
            redirectAttributes.addFlashAttribute("mensagem", produto.getNomeProduto() + " foi adicionado ao carrinho.");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/loja";
    }

    @PostMapping("/carrinho/atualizar/{produtoId}")
    public String atualizar(@PathVariable Integer produtoId, @RequestParam Integer quantidade,
            RedirectAttributes redirectAttributes) {
        try {
            Produto produto = buscarProduto(produtoId);
            carrinho.atualizar(produtoId, quantidade == null ? 0 : quantidade, produto.getQuantidadeEstoque());
            redirectAttributes.addFlashAttribute("mensagem", "Quantidade atualizada.");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/remover/{produtoId}")
    public String remover(@PathVariable Integer produtoId, RedirectAttributes redirectAttributes) {
        carrinho.remover(produtoId);
        redirectAttributes.addFlashAttribute("mensagem", "Produto removido do carrinho.");
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/limpar")
    public String limpar(RedirectAttributes redirectAttributes) {
        carrinho.limpar();
        redirectAttributes.addFlashAttribute("mensagem", "Carrinho esvaziado.");
        return "redirect:/carrinho";
    }

    private Produto buscarProduto(Integer produtoId) {
        return produtoService.buscarPorId(produtoId)
                .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
    }

    private void adicionarResumoCarrinho(Model model) {
        List<ItemCarrinho> itens = new ArrayList<>();
        List<Integer> produtosRemovidos = new ArrayList<>();

        carrinho.getQuantidades().forEach((produtoId, quantidade) -> produtoService.buscarPorId(produtoId)
                .ifPresentOrElse(produto -> itens.add(ItemCarrinho.de(produto, quantidade)),
                        () -> produtosRemovidos.add(produtoId)));
        produtosRemovidos.forEach(carrinho::remover);

        BigDecimal total = itens.stream()
                .map(ItemCarrinho::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("itensCarrinho", itens);
        model.addAttribute("quantidadeCarrinho", carrinho.getQuantidadeTotal());
        model.addAttribute("totalCarrinho", total);
    }
}
