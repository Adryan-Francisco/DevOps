package br.com.devops.devops.service;

import br.com.devops.devops.entity.Pedido;
import br.com.devops.devops.entity.Produto;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.repository.DisciplinaRepository;
import br.com.devops.devops.repository.PedidoRepository;
import br.com.devops.devops.repository.ProdutoRepository;
import br.com.devops.devops.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private static final int LIMITE_ESTOQUE_BAIXO = 5;

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoRepository pedidoRepository;

    public DashboardService(AlunoRepository alunoRepository, CursoRepository cursoRepository,
                            ProfessorRepository professorRepository, DisciplinaRepository disciplinaRepository,
                            ProdutoRepository produtoRepository, PedidoRepository pedidoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.professorRepository = professorRepository;
        this.disciplinaRepository = disciplinaRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public long totalAlunos() { return alunoRepository.count(); }
    public long totalCursos() { return cursoRepository.count(); }
    public long totalProfessores() { return professorRepository.count(); }
    public long totalDisciplinas() { return disciplinaRepository.count(); }
    public long totalProdutos() { return produtoRepository.count(); }
    public long totalPedidos() { return pedidoRepository.count(); }

    public List<Pedido> ultimosPedidos() {
        return pedidoRepository.findTop5ByOrderByIdPedidoDesc();
    }

    public List<Produto> produtosComEstoqueBaixo() {
        return produtoRepository.findByQuantidadeEstoqueLessThanEqualOrderByQuantidadeEstoque(LIMITE_ESTOQUE_BAIXO);
    }
}
