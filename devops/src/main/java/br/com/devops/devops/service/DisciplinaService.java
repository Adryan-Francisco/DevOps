package br.com.devops.devops.service;

import br.com.devops.devops.entity.Disciplina;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.CursoRepository;
import br.com.devops.devops.repository.DisciplinaRepository;
import br.com.devops.devops.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;

    public DisciplinaService(DisciplinaRepository disciplinaRepository, ProfessorRepository professorRepository,
                             CursoRepository cursoRepository) {
        this.disciplinaRepository = disciplinaRepository;
        this.professorRepository = professorRepository;
        this.cursoRepository = cursoRepository;
    }

    public List<Disciplina> listarTodas() {
        return disciplinaRepository.findAllByOrderByNomeDisciplina();
    }

    public Optional<Disciplina> buscarPorId(Integer id) {
        return disciplinaRepository.findById(id);
    }

    @Transactional
    public Disciplina salvar(Disciplina disciplina, Integer professorId, Integer cursoId) {
        vincular(disciplina, professorId, cursoId);
        return disciplinaRepository.save(disciplina);
    }

    @Transactional
    public Disciplina atualizar(Integer id, Disciplina dados, Integer professorId, Integer cursoId) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Disciplina não encontrada."));
        disciplina.setNomeDisciplina(dados.getNomeDisciplina());
        disciplina.setSiglaDisciplina(dados.getSiglaDisciplina());
        disciplina.setCargaHorariaDisciplina(dados.getCargaHorariaDisciplina());
        vincular(disciplina, professorId, cursoId);
        return disciplinaRepository.save(disciplina);
    }

    @Transactional
    public void deletar(Integer id) {
        disciplinaRepository.deleteById(id);
    }

    private void vincular(Disciplina disciplina, Integer professorId, Integer cursoId) {
        if (professorId == null) {
            throw new RegraNegocioException("Selecione um professor.");
        }
        if (cursoId == null) {
            throw new RegraNegocioException("Selecione um curso.");
        }
        disciplina.setProfessor(professorRepository.findById(professorId)
                .orElseThrow(() -> new RegraNegocioException("Professor não encontrado.")));
        disciplina.setCurso(cursoRepository.findById(cursoId)
                .orElseThrow(() -> new RegraNegocioException("Curso não encontrado.")));
    }
}
