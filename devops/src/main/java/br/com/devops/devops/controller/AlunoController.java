package br.com.devops.devops.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.service.AlunoService;

@Controller
@RequestMapping("/alunos")
public class AlunoController {

    @Autowired
    private AlunoService alunoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.getAllAlunos());
        return "aluno/listarAlunos";
    }

    @GetMapping("/novo")
    public String novoAluno(Model model) {
        model.addAttribute("aluno", new Aluno());
        return "aluno/formularioAluno";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Aluno aluno) {
        alunoService.saveAluno(aluno);
        return "redirect:/alunos";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Aluno aluno = alunoService.getAlunoById(id);
        model.addAttribute("aluno", aluno);
        return "aluno/formularioAluno";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Integer id) {
        alunoService.deleteAluno(id);
        return "redirect:/alunos";
    }
}