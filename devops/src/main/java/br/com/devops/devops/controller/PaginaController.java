package br.com.devops.devops.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import br.com.devops.devops.service.DashboardService;

@Controller
public class PaginaController {

    private final DashboardService dashboardService;

    public PaginaController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("totalAlunos", dashboardService.totalAlunos());
        model.addAttribute("totalCursos", dashboardService.totalCursos());
        model.addAttribute("totalProfessores", dashboardService.totalProfessores());
        model.addAttribute("totalDisciplinas", dashboardService.totalDisciplinas());
        model.addAttribute("totalProdutos", dashboardService.totalProdutos());
        model.addAttribute("totalPedidos", dashboardService.totalPedidos());
        model.addAttribute("ultimosPedidos", dashboardService.ultimosPedidos());
        model.addAttribute("estoqueBaixo", dashboardService.produtosComEstoqueBaixo());
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/", "/devops"})
    public String index(Authentication authentication) {
        boolean administrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        return administrador ? "redirect:/home" : "redirect:/loja";
    }
}
