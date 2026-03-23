package br.com.devops.devops.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.devops.devops.service.CursoService;

@Controller
@RequestMapping ("/cursos")
public class CursoController {

    //Injeção de dependência do serviço de cursos
    @Autowired
    private CursoService cursoService;
    
}
