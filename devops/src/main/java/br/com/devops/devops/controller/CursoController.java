package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Curso;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.CursoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/curso")
public class CursoController {

    private static final String FORMULARIO = "curso/formularioCurso";

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("cursos", cursoService.listarTodos());
        return "curso/listarCursos";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("curso", new Curso());
        return FORMULARIO;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return cursoService.buscarPorId(id).map(curso -> {
            model.addAttribute("curso", curso);
            return FORMULARIO;
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Curso não encontrado.");
            return "redirect:/curso/listar";
        });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Curso curso, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FORMULARIO;
        }
        curso.setIdCurso(null);
        cursoService.salvar(curso);
        redirectAttributes.addFlashAttribute("mensagem", "Curso cadastrado com sucesso!");
        return "redirect:/curso/listar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @Valid @ModelAttribute Curso curso, BindingResult bindingResult,
                            Model model, RedirectAttributes redirectAttributes) {
        curso.setIdCurso(id);
        if (bindingResult.hasErrors()) {
            return FORMULARIO;
        }
        try {
            cursoService.atualizar(id, curso);
            redirectAttributes.addFlashAttribute("mensagem", "Curso atualizado com sucesso!");
            return "redirect:/curso/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return FORMULARIO;
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            cursoService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Curso excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/curso/listar";
    }
}
