package br.com.devops.devops.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.devops.devops.entity.Aluno;
import java.util.List;

public interface AlunoRepository extends JpaRepository<Aluno, Integer> {

    List<Aluno> findAllByOrderByNomeAluno();

    boolean existsByRaAlunoAndIdAlunoNot(String raAluno, Integer idAluno);

    boolean existsByCpfAlunoAndIdAlunoNot(String cpfAluno, Integer idAluno);

    boolean existsByCursoIdCurso(Integer idCurso);
}
