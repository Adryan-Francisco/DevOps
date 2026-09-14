package br.com.devops.devops.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.devops.devops.entity.Curso;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.AlunoRepository;
import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.repository.DisciplinaRepository;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class CursoService {

    private final CursoRepository cursoRepository;
    private final AlunoRepository alunoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public CursoService(CursoRepository cursoRepository, AlunoRepository alunoRepository,
                        DisciplinaRepository disciplinaRepository) {
        this.cursoRepository = cursoRepository;
        this.alunoRepository = alunoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public List<Curso> listarTodos() {
        return cursoRepository.findAllByOrderByNomeCurso();
    }

    public Optional<Curso> buscarPorId(Integer id) {
        return cursoRepository.findById(id);
    }

    @Transactional
    public Curso salvar(Curso curso) {
        return cursoRepository.save(curso);
    }

    @Transactional
    public Curso atualizar(Integer id, Curso cursoAtualizado) {
        Curso curso = cursoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Curso não encontrado."));
        curso.setNomeCurso(cursoAtualizado.getNomeCurso());
        curso.setPeriodoCurso(cursoAtualizado.getPeriodoCurso());
        curso.setCargahorariaCurso(cursoAtualizado.getCargahorariaCurso());
        return cursoRepository.save(curso);
    }

    @Transactional
    public void deletar(Integer id) {
        if (alunoRepository.existsByCursoIdCurso(id)) {
            throw new RegraNegocioException("Este curso possui alunos matriculados e não pode ser excluído.");
        }
        if (disciplinaRepository.existsByCursoIdCurso(id)) {
            throw new RegraNegocioException("Este curso possui disciplinas e não pode ser excluído.");
        }
        cursoRepository.deleteById(id);
    }
}
