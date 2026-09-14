package br.com.devops.devops.controller;

import java.security.Principal;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import br.com.devops.devops.service.UsuarioService;

// Disponibiliza o usuário logado para o menu lateral de todas as páginas
@ControllerAdvice
public class UsuarioLogadoAdvice {

    private final UsuarioService usuarioService;

    public UsuarioLogadoAdvice(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @ModelAttribute
    public void adicionarUsuarioLogado(Model model, Principal principal) {
        if (principal == null) {
            return;
        }
        usuarioService.buscarPorLogin(principal.getName()).ifPresent(usuario -> {
            model.addAttribute("usuarioLogado", usuario);
            model.addAttribute("usuarioAdmin", "ADMIN".equalsIgnoreCase(usuario.getRoleUsuario()));
        });
    }
}
