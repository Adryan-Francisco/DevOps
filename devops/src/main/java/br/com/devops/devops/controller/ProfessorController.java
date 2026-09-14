package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Professor;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.ProfessorService;
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
@RequestMapping("/professor")
public class ProfessorController {

    private static final String FORMULARIO = "professor/formularioProfessor";

    private final ProfessorService professorService;

    public ProfessorController(ProfessorService professorService) {
        this.professorService = professorService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("professores", professorService.listarTodos());
        return "professor/listarProfessores";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("professor", new Professor());
        return FORMULARIO;
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return professorService.buscarPorId(id).map(professor -> {
            model.addAttribute("professor", professor);
            return FORMULARIO;
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Professor não encontrado.");
            return "redirect:/professor/listar";
        });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Professor professor, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FORMULARIO;
        }
        professor.setIdProfessor(null);
        professorService.salvar(professor);
        redirectAttributes.addFlashAttribute("mensagem", "Professor cadastrado com sucesso!");
        return "redirect:/professor/listar";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @Valid @ModelAttribute Professor professor,
                            BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        professor.setIdProfessor(id);
        if (bindingResult.hasErrors()) {
            return FORMULARIO;
        }
        try {
            professorService.atualizar(id, professor);
            redirectAttributes.addFlashAttribute("mensagem", "Professor atualizado com sucesso!");
            return "redirect:/professor/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return FORMULARIO;
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            professorService.deletar(id);
            redirectAttributes.addFlashAttribute("mensagem", "Professor excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/professor/listar";
    }
}
