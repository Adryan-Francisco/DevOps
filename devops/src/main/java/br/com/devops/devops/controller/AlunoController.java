package br.com.devops.devops.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import br.com.devops.devops.service.AlunoService;
import org.springframework.ui.Model;
import br.com.devops.devops.entity.Aluno;



@Controller
@RequestMapping("/alunos")
public class AlunoController {

    // Injeção de dependência do serviço de alunos
    @Autowired
    private AlunoService alunoService;

@GetMapping
public String listarRaiz(Model model) {
    model.addAttribute("alunos", alunoService.getAllAlunos());
    return "aluno/listarAlunos"; // <-- corrigido
}
    //Metodo para salvar aluno
    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.saveAluno(aluno);
        return "redirect:/alunos/listar";
    }  

    //Método para listar todos os alunos
   @GetMapping("/listar")
public String listar(Model model) {
    model.addAttribute("alunos", alunoService.getAllAlunos());
    return "aluno/listarAlunos"; // <-- corrigido
    }
    // Método para criar um novo aluno e abrir o formulário de cadastro
    @GetMapping("/novo")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/formularioAluno";
    }
    
    //Metodo para atualizar aluno

    



    
}
