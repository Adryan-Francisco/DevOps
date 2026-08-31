package br.com.devops.devops.service;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProdutoService {
    private static final long TAMANHO_MAXIMO_FOTO = 5 * 1024 * 1024;
    private static final Set<String> TIPOS_FOTO_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp");

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> listarTodos() { return produtoRepository.findAll(); }
    public Optional<Produto> buscarPorId(Integer id) { return produtoRepository.findById(id); }
    public Produto salvar(Produto produto, MultipartFile fotoArquivo) {
        produto.setFoto(null);
        produto.setTipoFoto(null);
        atualizarFoto(produto, fotoArquivo);
        return produtoRepository.save(produto);
    }
    public void deletar(Integer id) { produtoRepository.deleteById(id); }

    public Produto atualizar(Integer id, Produto dados, MultipartFile fotoArquivo) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado"));
        produto.setNomeProduto(dados.getNomeProduto());
        produto.setDescricaoProduto(dados.getDescricaoProduto());
        produto.setPrecoProduto(dados.getPrecoProduto());
        produto.setQuantidadeEstoque(dados.getQuantidadeEstoque());
        atualizarFoto(produto, fotoArquivo);
        return produtoRepository.save(produto);
    }

    private void atualizarFoto(Produto produto, MultipartFile fotoArquivo) {
        if (fotoArquivo == null || fotoArquivo.isEmpty()) {
            return;
        }

        String tipoFoto = fotoArquivo.getContentType();
        if (tipoFoto == null || !TIPOS_FOTO_PERMITIDOS.contains(tipoFoto)) {
            throw new IllegalArgumentException("Envie uma foto JPG, PNG, GIF ou WEBP");
        }
        if (fotoArquivo.getSize() > TAMANHO_MAXIMO_FOTO) {
            throw new IllegalArgumentException("A foto deve ter no máximo 5 MB");
        }

        try {
            byte[] conteudo = fotoArquivo.getBytes();
            if (!assinaturaCompativel(tipoFoto, conteudo)) {
                throw new IllegalArgumentException("O conteúdo do arquivo não corresponde ao tipo da foto");
            }
            produto.setFoto(conteudo);
            produto.setTipoFoto(tipoFoto);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Não foi possível ler a foto enviada", ex);
        }
    }

    private boolean assinaturaCompativel(String tipoFoto, byte[] conteudo) {
        return switch (tipoFoto) {
            case "image/jpeg" -> iniciaCom(conteudo, 0xFF, 0xD8, 0xFF);
            case "image/png" -> iniciaCom(conteudo, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A);
            case "image/gif" -> iniciaCom(conteudo, 0x47, 0x49, 0x46, 0x38)
                    && conteudo.length > 5 && (conteudo[4] == '7' || conteudo[4] == '9') && conteudo[5] == 'a';
            case "image/webp" -> iniciaCom(conteudo, 0x52, 0x49, 0x46, 0x46)
                    && iniciaComNaPosicao(conteudo, 8, 0x57, 0x45, 0x42, 0x50);
            default -> false;
        };
    }

    private boolean iniciaCom(byte[] conteudo, int... assinatura) {
        return iniciaComNaPosicao(conteudo, 0, assinatura);
    }

    private boolean iniciaComNaPosicao(byte[] conteudo, int posicao, int... assinatura) {
        if (conteudo.length < posicao + assinatura.length) {
            return false;
        }
        for (int indice = 0; indice < assinatura.length; indice++) {
            if ((conteudo[posicao + indice] & 0xFF) != assinatura[indice]) {
                return false;
            }
        }
        return true;
    }
}
