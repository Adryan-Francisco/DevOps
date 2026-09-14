package br.com.devops.devops.repository;

import br.com.devops.devops.entity.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DisciplinaRepository extends JpaRepository<Disciplina, Integer> {

    List<Disciplina> findAllByOrderByNomeDisciplina();

    boolean existsByCursoIdCurso(Integer idCurso);

    boolean existsByProfessorIdProfessor(Integer idProfessor);
}
