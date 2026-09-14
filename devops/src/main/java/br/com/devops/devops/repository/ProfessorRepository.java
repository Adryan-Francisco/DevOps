package br.com.devops.devops.repository;

import br.com.devops.devops.entity.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Integer> {

    List<Professor> findAllByOrderByNomeProfessor();
}
