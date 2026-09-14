package br.com.devops.devops.service;

import br.com.devops.devops.entity.Professor;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.repository.DisciplinaRepository;
import br.com.devops.devops.repository.ProfessorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProfessorService {

    private final ProfessorRepository professorRepository;
    private final DisciplinaRepository disciplinaRepository;

    public ProfessorService(ProfessorRepository professorRepository, DisciplinaRepository disciplinaRepository) {
        this.professorRepository = professorRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public List<Professor> listarTodos() {
        return professorRepository.findAllByOrderByNomeProfessor();
    }

    public Optional<Professor> buscarPorId(Integer id) {
        return professorRepository.findById(id);
    }

    @Transactional
    public Professor salvar(Professor professor) {
        return professorRepository.save(professor);
    }

    @Transactional
    public Professor atualizar(Integer id, Professor professorAtualizado) {
        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Professor não encontrado."));
        professor.setNomeProfessor(professorAtualizado.getNomeProfessor());
        professor.setTelefoneProfessor(professorAtualizado.getTelefoneProfessor());
        professor.setGraduacaoProfessor(professorAtualizado.getGraduacaoProfessor());
        professor.setRmProfessor(professorAtualizado.getRmProfessor());
        return professorRepository.save(professor);
    }

    @Transactional
    public void deletar(Integer id) {
        if (disciplinaRepository.existsByProfessorIdProfessor(id)) {
            throw new RegraNegocioException("Este professor leciona disciplinas e não pode ser excluído.");
        }
        professorRepository.deleteById(id);
    }
}
