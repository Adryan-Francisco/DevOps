package br.com.devops.devops.service;

import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.ProdutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProdutoServiceTest {

    private ProdutoRepository produtoRepository;
    private ProdutoService produtoService;

    @BeforeEach
    void setUp() {
        produtoRepository = mock(ProdutoRepository.class);
        produtoService = new ProdutoService(produtoRepository);
    }

    @Test
    void deveSalvarFotoESeuTipoNoProduto() {
        byte[] png = new byte[] {
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0x01
        };
        MockMultipartFile foto = new MockMultipartFile(
                "fotoArquivo", "produto.png", "image/png", png);
        Produto produto = new Produto();
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto salvo = produtoService.salvar(produto, foto);

        assertArrayEquals(png, salvo.getFoto());
        assertEquals("image/png", salvo.getTipoFoto());
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveRejeitarArquivoQueNaoSejaImagemPermitida() {
        MockMultipartFile arquivo = new MockMultipartFile(
                "fotoArquivo", "produto.txt", "text/plain", "conteudo".getBytes());
        Produto produto = new Produto();

        IllegalArgumentException erro = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.salvar(produto, arquivo));

        assertEquals("Envie uma foto JPG, PNG, GIF ou WEBP", erro.getMessage());
        verify(produtoRepository, never()).save(produto);
    }

    @Test
    void deveManterFotoAtualQuandoEdicaoNaoEnviaNovoArquivo() {
        byte[] fotoAtual = new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
        Produto produtoAtual = new Produto();
        produtoAtual.setFoto(fotoAtual);
        produtoAtual.setTipoFoto("image/jpeg");
        Produto novosDados = new Produto();
        novosDados.setNomeProduto("Produto atualizado");

        when(produtoRepository.findById(1)).thenReturn(Optional.of(produtoAtual));
        when(produtoRepository.save(produtoAtual)).thenReturn(produtoAtual);

        Produto atualizado = produtoService.atualizar(1, novosDados, null);

        assertArrayEquals(fotoAtual, atualizado.getFoto());
        assertEquals("image/jpeg", atualizado.getTipoFoto());
    }
}
