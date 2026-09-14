package br.com.devops.devops.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.devops.devops.entity.Aluno;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.AlunoService;
import br.com.devops.devops.service.CursoService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/aluno")
public class AlunoController {

    private static final String FORMULARIO = "aluno/formularioAluno";

    private final AlunoService alunoService;
    private final CursoService cursoService;

    public AlunoController(AlunoService alunoService, CursoService cursoService) {
        this.alunoService = alunoService;
        this.cursoService = cursoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("alunos", alunoService.listarTodos());
        return "aluno/listarAlunos";
    }

    @GetMapping("/formulario")
    public String novoAluno(Model model) {
        return exibirFormulario(new Aluno(), null, model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return alunoService.buscarPorId(id)
                .map(aluno -> exibirFormulario(aluno, aluno.getCurso() != null ? aluno.getCurso().getIdCurso() : null, model))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Aluno não encontrado.");
                    return "redirect:/aluno/listar";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Aluno aluno, BindingResult bindingResult,
                         @RequestParam(required = false) Integer cursoId, Model model,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return exibirFormulario(aluno, cursoId, model);
        }
        try {
            boolean novo = aluno.getIdAluno() == null;
            alunoService.salvar(aluno, cursoId);
            redirectAttributes.addFlashAttribute("mensagem", novo ? "Aluno cadastrado com sucesso!" : "Aluno atualizado com sucesso!");
            return "redirect:/aluno/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return exibirFormulario(aluno, cursoId, model);
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Aluno excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/aluno/listar";
    }

    private String exibirFormulario(Aluno aluno, Integer cursoId, Model model) {
        model.addAttribute("aluno", aluno);
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("cursos", cursoService.listarTodos());
        return FORMULARIO;
    }
}
