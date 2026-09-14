package br.com.devops.devops;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.entity.StatusPedido;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.repository.PedidoRepository;
import br.com.devops.devops.repository.ProdutoRepository;
import br.com.devops.devops.repository.ProfessorRepository;
import br.com.devops.devops.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
class FluxoSistemaIntegrationTest {

    private static final Pattern CSRF = Pattern.compile("name=\"_csrf\" value=\"([^\"]+)\"");

    @Autowired private MockMvc mockMvc;
    @Autowired private CursoRepository cursoRepository;
    @Autowired private ProfessorRepository professorRepository;
    @Autowired private AlunoRepository alunoRepository;
    @Autowired private ProdutoRepository produtoRepository;
    @Autowired private PedidoRepository pedidoRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Test
    void paginasProtegidasExigemLogin() throws Exception {
        mockMvc.perform(get("/home")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/login")).andExpect(status().isOk());
    }

    @Test
    void loginRedirecionaConformeOPerfil() throws Exception {
        MockHttpSession sessaoAdmin = new MockHttpSession();
        mockMvc.perform(post("/login").session(sessaoAdmin).param("_csrf", csrf(sessaoAdmin, "/login"))
                        .param("username", "admin").param("password", "admin123"))
                .andExpect(redirectedUrl("/home"));

        MockHttpSession sessaoCliente = new MockHttpSession();
        mockMvc.perform(post("/login").session(sessaoCliente).param("_csrf", csrf(sessaoCliente, "/login"))
                        .param("username", "joao").param("password", "joao123"))
                .andExpect(redirectedUrl("/loja"));
    }

    @Test
    void somenteAdminAcessaUsuarios() throws Exception {
        mockMvc.perform(get("/usuario/listar").session(sessao("joao", "PROFESSOR")))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/usuario/listar").session(sessao("admin", "ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void direcionaPerfisEProtegeOPainelAdministrativo() throws Exception {
        mockMvc.perform(get("/").session(sessao("admin", "ADMIN")))
                .andExpect(redirectedUrl("/home"));

        MockHttpSession sessaoCliente = sessao("joao", "PROFESSOR");
        mockMvc.perform(get("/").session(sessaoCliente))
                .andExpect(redirectedUrl("/loja"));
        mockMvc.perform(get("/loja").session(sessaoCliente))
                .andExpect(status().isOk());
        mockMvc.perform(get("/home").session(sessaoCliente))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/produto/listar").session(sessaoCliente))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioAdicionaEAtualizaProdutoNoCarrinho() throws Exception {
        Produto produto = new Produto();
        produto.setNomeProduto("Mochila Marketplace");
        produto.setDescricaoProduto("Produto para validar o carrinho");
        produto.setPrecoProduto(new BigDecimal("89.90"));
        produto.setQuantidadeEstoque(8);
        produto = produtoRepository.save(produto);

        MockHttpSession sessaoCliente = sessao("joao", "PROFESSOR");
        String token = csrf(sessaoCliente, "/loja");

        mockMvc.perform(post("/carrinho/adicionar/" + produto.getIdProduto()).session(sessaoCliente)
                        .param("_csrf", token).param("quantidade", "2"))
                .andExpect(redirectedUrl("/loja"))
                .andExpect(flash().attributeExists("mensagem"));

        mockMvc.perform(get("/carrinho").session(sessaoCliente))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Mochila Marketplace")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("2 itens")));

        mockMvc.perform(post("/carrinho/atualizar/" + produto.getIdProduto()).session(sessaoCliente)
                        .param("_csrf", token).param("quantidade", "9"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    void formularioSemTokenCsrfEhRecusado() throws Exception {
        mockMvc.perform(post("/curso/salvar").session(sessao("admin", "ADMIN"))
                        .param("nomeCurso", "Sem token").param("periodoCurso", "Noturno").param("cargahorariaCurso", "10"))
                .andExpect(status().isForbidden());
    }

    @Test
    void editarUsuarioSemInformarSenhaMantemSenhaAtual() throws Exception {
        MockHttpSession sessao = sessao("admin", "ADMIN");
        var maria = usuarioRepository.findByLoginUsuario("maria").orElseThrow();
        String senhaAntes = maria.getSenhaUsuario();

        mockMvc.perform(post("/usuario/salvar").session(sessao).param("_csrf", csrf(sessao, "/usuario/formulario"))
                        .param("idUsuario", maria.getIdUsuario().toString())
                        .param("nomeUsuario", "Maria Santos Atualizada").param("emailUsuario", "maria@devops.com")
                        .param("loginUsuario", "maria").param("roleUsuario", "SECRETARIA").param("senhaUsuario", ""))
                .andExpect(redirectedUrl("/usuario/listar"));

        var atualizada = usuarioRepository.findByLoginUsuario("maria").orElseThrow();
        assertThat(atualizada.getNomeUsuario()).isEqualTo("Maria Santos Atualizada");
        assertThat(atualizada.getSenhaUsuario()).isEqualTo(senhaAntes);
    }

    @Test
    void fluxoCompletoDeCadastrosPedidoEEstoque() throws Exception {
        MockHttpSession sessao = sessao("admin", "ADMIN");
        String token = csrf(sessao, "/curso/formulario");

        // Curso e professor
        mockMvc.perform(post("/curso/salvar").session(sessao).param("_csrf", token)
                        .param("nomeCurso", "Fluxo DSM").param("periodoCurso", "Noturno").param("cargahorariaCurso", "2400"))
                .andExpect(redirectedUrl("/curso/listar"));
        Integer cursoId = cursoRepository.findAll().stream()
                .filter(c -> c.getNomeCurso().equals("Fluxo DSM")).findFirst().orElseThrow().getIdCurso();

        mockMvc.perform(post("/professor/salvar").session(sessao).param("_csrf", token)
                        .param("nomeProfessor", "Prof. Fluxo").param("telefoneProfessor", "(11) 98888-7777")
                        .param("graduacaoProfessor", "Mestre").param("rmProfessor", "12345-6"))
                .andExpect(redirectedUrl("/professor/listar"));
        Integer professorId = professorRepository.findAll().stream()
                .filter(p -> p.getNomeProfessor().equals("Prof. Fluxo")).findFirst().orElseThrow().getIdProfessor();

        // Disciplina vinculada a curso e professor
        mockMvc.perform(post("/disciplina/salvar").session(sessao).param("_csrf", token)
                        .param("nomeDisciplina", "Integração").param("siglaDisciplina", "INT").param("cargaHorariaDisciplina", "80")
                        .param("cursoId", cursoId.toString()).param("professorId", professorId.toString()))
                .andExpect(redirectedUrl("/disciplina/listar"));
        mockMvc.perform(get("/disciplina/listar").session(sessao))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Fluxo DSM")));

        // Validação: CPF inválido reexibe o formulário com a mensagem
        mockMvc.perform(post("/aluno/salvar").session(sessao).param("_csrf", token)
                        .param("nomeAluno", "Aluno Fluxo").param("emailAluno", "fluxo@email.com")
                        .param("telefoneAluno", "(11) 91234-5678").param("enderecoAluno", "Rua A, 1")
                        .param("cpfAluno", "123").param("raAluno", "RA-FLUXO").param("cursoId", cursoId.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Informe o CPF no formato")));

        mockMvc.perform(post("/aluno/salvar").session(sessao).param("_csrf", token)
                        .param("nomeAluno", "Aluno Fluxo").param("emailAluno", "fluxo@email.com")
                        .param("telefoneAluno", "(11) 91234-5678").param("enderecoAluno", "Rua A, 1")
                        .param("cpfAluno", "123.456.789-00").param("raAluno", "RA-FLUXO").param("cursoId", cursoId.toString()))
                .andExpect(redirectedUrl("/aluno/listar"));
        Aluno aluno = alunoRepository.findAll().stream()
                .filter(a -> a.getRaAluno().equals("RA-FLUXO")).findFirst().orElseThrow();

        // Curso com aluno não pode ser excluído
        mockMvc.perform(post("/curso/deletar/" + cursoId).session(sessao).param("_csrf", token))
                .andExpect(flash().attributeExists("erro"));

        // Produto com 10 unidades
        mockMvc.perform(post("/produto/salvar").session(sessao).param("_csrf", token)
                        .param("nomeProduto", "Caneca Fluxo").param("descricaoProduto", "Caneca de teste")
                        .param("precoProduto", "25.50").param("quantidadeEstoque", "10"))
                .andExpect(redirectedUrl("/produto/listar"));
        Integer produtoId = produtoRepository.findAll().stream()
                .filter(p -> p.getNomeProduto().equals("Caneca Fluxo")).findFirst().orElseThrow().getIdProduto();

        // Pedido
        mockMvc.perform(post("/pedido/salvar").session(sessao).param("_csrf", token)
                        .param("alunoId", aluno.getIdAluno().toString()).param("dataPedido", "2026-09-14")
                        .param("status", "PENDENTE"))
                .andExpect(redirectedUrlPattern("/item-pedido/listar/*"));
        Pedido pedido = pedidoRepository.findAll().stream()
                .filter(p -> p.getAluno() != null && p.getAluno().getIdAluno().equals(aluno.getIdAluno()))
                .findFirst().orElseThrow();
        Integer pedidoId = pedido.getIdPedido();

        // Adiciona 3 unidades duas vezes: vira um único item com 6 unidades
        adicionarItem(sessao, token, pedidoId, produtoId, 3);
        adicionarItem(sessao, token, pedidoId, produtoId, 3);
        Pedido comItens = pedidoRepository.findById(pedidoId).orElseThrow();
        assertThat(comItens.getItens()).hasSize(1);
        assertThat(comItens.getValorTotal()).isEqualByComparingTo("153.00");
        assertThat(estoque(produtoId)).isEqualTo(4);

        mockMvc.perform(get("/item-pedido/listar/" + pedidoId).session(sessao))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("R$")));

        // Não permite passar do estoque
        mockMvc.perform(post("/item-pedido/salvar").session(sessao).param("_csrf", token)
                        .param("pedidoId", pedidoId.toString()).param("produtoId", produtoId.toString()).param("quantidade", "50"))
                .andExpect(flash().attributeExists("erro"));
        assertThat(estoque(produtoId)).isEqualTo(4);

        // Diminui a quantidade para 2: devolve 4 ao estoque
        Integer itemId = comItens.getItens().get(0).getIdItemDoPedido();
        mockMvc.perform(post("/item-pedido/atualizar/" + itemId).session(sessao).param("_csrf", token)
                        .param("pedidoId", pedidoId.toString()).param("quantidade", "2"))
                .andExpect(flash().attributeExists("mensagem"));
        assertThat(estoque(produtoId)).isEqualTo(8);
        assertThat(pedidoRepository.findById(pedidoId).orElseThrow().getValorTotal()).isEqualByComparingTo("51.00");

        // Produto em pedido e aluno com pedido não podem ser excluídos
        mockMvc.perform(post("/produto/deletar/" + produtoId).session(sessao).param("_csrf", token))
                .andExpect(flash().attributeExists("erro"));
        mockMvc.perform(post("/aluno/deletar/" + aluno.getIdAluno()).session(sessao).param("_csrf", token))
                .andExpect(flash().attributeExists("erro"));

        // Cancelar devolve ao estoque e bloqueia alteração de itens
        mockMvc.perform(post("/pedido/atualizar/" + pedidoId).session(sessao).param("_csrf", token)
                        .param("alunoId", aluno.getIdAluno().toString()).param("dataPedido", "2026-09-14")
                        .param("status", "CANCELADO"))
                .andExpect(redirectedUrl("/pedido/listar"));
        assertThat(estoque(produtoId)).isEqualTo(10);
        assertThat(pedidoRepository.findById(pedidoId).orElseThrow().getStatus()).isEqualTo(StatusPedido.CANCELADO);
        mockMvc.perform(post("/item-pedido/salvar").session(sessao).param("_csrf", token)
                        .param("pedidoId", pedidoId.toString()).param("produtoId", produtoId.toString()).param("quantidade", "1"))
                .andExpect(flash().attributeExists("erro"));

        // Excluir pedido cancelado não mexe no estoque de novo
        mockMvc.perform(post("/pedido/deletar/" + pedidoId).session(sessao).param("_csrf", token))
                .andExpect(redirectedUrl("/pedido/listar"));
        assertThat(pedidoRepository.findById(pedidoId)).isEmpty();
        assertThat(estoque(produtoId)).isEqualTo(10);

        // Todas as telas principais renderizam
        for (String url : List.of("/home", "/aluno/listar", "/aluno/formulario", "/curso/listar", "/curso/editar/" + cursoId,
                "/professor/listar", "/disciplina/listar", "/disciplina/formulario", "/produto/listar",
                "/produto/editar/" + produtoId, "/pedido/listar", "/pedido/formulario", "/usuario/listar",
                "/usuario/formulario")) {
            mockMvc.perform(get(url).session(sessao)).andExpect(status().isOk());
        }
    }

    private void adicionarItem(MockHttpSession sessao, String token, Integer pedidoId, Integer produtoId, int qtd)
            throws Exception {
        mockMvc.perform(post("/item-pedido/salvar").session(sessao).param("_csrf", token)
                        .param("pedidoId", pedidoId.toString()).param("produtoId", produtoId.toString())
                        .param("quantidade", String.valueOf(qtd)))
                .andExpect(flash().attributeExists("mensagem"));
    }

    private int estoque(Integer produtoId) {
        return produtoRepository.findById(produtoId).map(Produto::getQuantidadeEstoque).orElseThrow();
    }

    private MockHttpSession sessao(String login, String role) {
        var autenticacao = UsernamePasswordAuthenticationToken.authenticated(
                login, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
        SecurityContext contexto = SecurityContextHolder.createEmptyContext();
        contexto.setAuthentication(autenticacao);
        MockHttpSession sessao = new MockHttpSession();
        sessao.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, contexto);
        return sessao;
    }

    // Lê o token CSRF que o Thymeleaf injeta nos formulários
    private String csrf(MockHttpSession sessao, String urlFormulario) throws Exception {
        String html = mockMvc.perform(get(urlFormulario).session(sessao))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        Matcher matcher = CSRF.matcher(html);
        assertThat(matcher.find()).as("token CSRF no formulário").isTrue();
        return matcher.group(1);
    }
}
