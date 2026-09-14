package br.com.devops.devops.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.entity.Curso;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.repository.PedidoRepository;

@Service
@Transactional(readOnly = true)
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;
    private final PedidoRepository pedidoRepository;

    public AlunoService(AlunoRepository alunoRepository, CursoRepository cursoRepository,
                        PedidoRepository pedidoRepository) {
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    public List<Aluno> listarTodos() {
        return alunoRepository.findAllByOrderByNomeAluno();
    }

    public Optional<Aluno> buscarPorId(Integer id) {
        return alunoRepository.findById(id);
    }

    @Transactional
    public Aluno salvar(Aluno aluno, Integer cursoId) {
        // Ids começam em 1, então 0 representa "nenhum aluno" ao cadastrar
        Integer idAtual = aluno.getIdAluno() == null ? 0 : aluno.getIdAluno();

        if (alunoRepository.existsByRaAlunoAndIdAlunoNot(aluno.getRaAluno(), idAtual)) {
            throw new RegraNegocioException("Já existe um aluno com este RA.");
        }
        if (alunoRepository.existsByCpfAlunoAndIdAlunoNot(aluno.getCpfAluno(), idAtual)) {
            throw new RegraNegocioException("Já existe um aluno com este CPF.");
        }

        aluno.setCurso(buscarCurso(cursoId));
        return alunoRepository.save(aluno);
    }

    @Transactional
    public void deletar(Integer id) {
        if (pedidoRepository.existsByAlunoIdAluno(id)) {
            throw new RegraNegocioException("Este aluno possui pedidos e não pode ser excluído.");
        }
        alunoRepository.deleteById(id);
    }

    private Curso buscarCurso(Integer cursoId) {
        if (cursoId == null) {
            throw new RegraNegocioException("Selecione um curso.");
        }
        return cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RegraNegocioException("Curso não encontrado."));
    }
}
