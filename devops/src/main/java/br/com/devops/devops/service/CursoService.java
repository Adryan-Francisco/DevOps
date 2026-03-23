package br.com.devops.devops.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.devops.devops.repository.CursoRepository;

@Service
public class CursoService {

    // Injeção de dependência do repositório de cursos
    @Autowired 
    private CursoRepository cursoRepository;
    
}
