package br.com.devops.devops.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.com.devops.devops.entity.Usuario;
import br.com.devops.devops.exception.RegraNegocioException;
import br.com.devops.devops.service.UsuarioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/usuario")
public class UsuarioController {

    private static final String[] ROLES = { "ADMIN", "ALUNO", "PROFESSOR", "SECRETARIA", "USER" };
    private static final String FORMULARIO = "usuario/formularioUsuario";

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/listar")
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "usuario/listarUsuarios";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("roles", ROLES);
        return FORMULARIO;
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Usuario usuario, BindingResult bindingResult, Model model,
            RedirectAttributes redirectAttributes) {
        boolean novo = usuario.getIdUsuario() == null;
        String senha = usuario.getSenhaUsuario();

        if (novo && (senha == null || senha.isBlank())) {
            bindingResult.rejectValue("senhaUsuario", "senhaUsuario.required", "A senha é obrigatória.");
        } else if (senha != null && !senha.isBlank() && senha.length() < 6) {
            bindingResult.rejectValue("senhaUsuario", "senhaUsuario.size", "A senha deve ter pelo menos 6 caracteres.");
        }
        if (usuario.getLoginUsuario() != null && usuarioService.loginEmUso(usuario.getLoginUsuario(), usuario.getIdUsuario())) {
            bindingResult.rejectValue("loginUsuario", "loginUsuario.unique", "Este login já está em uso.");
        }
        if (usuario.getEmailUsuario() != null && usuarioService.emailEmUso(usuario.getEmailUsuario(), usuario.getIdUsuario())) {
            bindingResult.rejectValue("emailUsuario", "emailUsuario.unique", "Este email já está em uso.");
        }

        if (bindingResult.hasErrors()) {
            usuario.setSenhaUsuario(null);
            model.addAttribute("roles", ROLES);
            return FORMULARIO;
        }

        try {
            usuarioService.salvar(usuario);
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
            return "redirect:/usuario/listar";
        }
        redirectAttributes.addFlashAttribute("mensagem", novo ? "Usuário cadastrado com sucesso!" : "Usuário atualizado com sucesso!");
        return "redirect:/usuario/listar";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        return usuarioService.buscarPorId(id).map(usuario -> {
            usuario.setSenhaUsuario(null);
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", ROLES);
            return FORMULARIO;
        }).orElseGet(() -> {
            redirectAttributes.addFlashAttribute("erro", "Usuário não encontrado.");
            return "redirect:/usuario/listar";
        });
    }

    @PostMapping("/deletar/{id}")
    public String deletar(@PathVariable Integer id, Principal principal, RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deletar(id, principal.getName());
            redirectAttributes.addFlashAttribute("mensagem", "Usuário excluído com sucesso!");
        } catch (RegraNegocioException ex) {
            redirectAttributes.addFlashAttribute("erro", ex.getMessage());
        }
        return "redirect:/usuario/listar";
    }
}
