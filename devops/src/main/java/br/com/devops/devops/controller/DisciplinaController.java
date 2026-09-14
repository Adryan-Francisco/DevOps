package br.com.devops.devops.controller;

import br.com.devops.devops.entity.Disciplina;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.CursoService;
import br.com.devops.devops.service.DisciplinaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/disciplina")
public class DisciplinaController {

    private static final String FORMULARIO = "disciplina/formularioDisciplina";

    private final DisciplinaService disciplinaService;
    private final ProfessorService professorService;
    private final CursoService cursoService;

    public DisciplinaController(DisciplinaService disciplinaService, ProfessorService professorService,
                                CursoService cursoService) {
        this.disciplinaService = disciplinaService;
        this.professorService = professorService;
        this.cursoService = cursoService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("disciplinas", disciplinaService.listarTodas());
        return "disciplina/listarDisciplinas";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        return exibirFormulario(new Disciplina(), null, null, model);
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return disciplinaService.buscarPorId(id)
                .map(disciplina -> exibirFormulario(disciplina,
                        disciplina.getProfessor().getIdProfessor(),
                        disciplina.getCurso().getIdCurso(), model))
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Disciplina não encontrada.");
                    return "redirect:/disciplina/listar";
                });
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Disciplina disciplina, BindingResult bindingResult,
                         @RequestParam(required = false) Integer professorId,
                         @RequestParam(required = false) Integer cursoId,
                         Model model, RedirectAttributes redirectAttributes) {
        disciplina.setIdDisciplina(null);
        if (bindingResult.hasErrors()) {
            return exibirFormulario(disciplina, professorId, cursoId, model);
        }
        try {
            disciplinaService.salvar(disciplina, professorId, cursoId);
            redirectAttributes.addFlashAttribute("mensagem", "Disciplina cadastrada com sucesso!");
            return "redirect:/disciplina/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return exibirFormulario(disciplina, professorId, cursoId, model);
        }
    }

    @PostMapping("/atualizar/{id}")
    public String atualizar(@PathVariable Integer id, @Valid @ModelAttribute Disciplina disciplina,
                            BindingResult bindingResult,
                            @RequestParam(required = false) Integer professorId,
                            @RequestParam(required = false) Integer cursoId,
                            Model model, RedirectAttributes redirectAttributes) {
        disciplina.setIdDisciplina(id);
        if (bindingResult.hasErrors()) {
            return exibirFormulario(disciplina, professorId, cursoId, model);
        }
        try {
            disciplinaService.atualizar(id, disciplina, professorId, cursoId);
            redirectAttributes.addFlashAttribute("mensagem", "Disciplina atualizada com sucesso!");
            return "redirect:/disciplina/listar";
        } catch (RegraNegocioException ex) {
            model.addAttribute("erro", ex.getMessage());
            return exibirFormulario(disciplina, professorId, cursoId, model);
        }
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        disciplinaService.deletar(id);
        redirectAttributes.addFlashAttribute("mensagem", "Disciplina excluída com sucesso!");
        return "redirect:/disciplina/listar";
    }

    private String exibirFormulario(Disciplina disciplina, Integer professorId, Integer cursoId, Model model) {
        model.addAttribute("disciplina", disciplina);
        model.addAttribute("professorId", professorId);
        model.addAttribute("cursoId", cursoId);
        model.addAttribute("professores", professorService.listarTodos());
        model.addAttribute("cursos", cursoService.listarTodos());
        return FORMULARIO;
    }
}
