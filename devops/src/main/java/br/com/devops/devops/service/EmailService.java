package br.com.devops.devops.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void enviarEmailRecuperacaoSenha(String email, String token, String nomeUsuario) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperação de Senha - Sistema DevOps");
            message.setFrom("seu_email@gmail.com");
            
            String linkReset = "http://localhost:8080/redefinir-senha?token=" + token;
            String corpo = "Olá " + nomeUsuario + ",\n\n"
                    + "Você solicitou uma recuperação de senha. Clique no link abaixo para redefinir sua senha:\n\n"
                    + linkReset + "\n\n"
                    + "Este link expira em 24 horas.\n\n"
                    + "Se você não solicitou isto, ignore este email.\n\n"
                    + "Atenciosamente,\nSistema DevOps";
            
            message.setText(corpo);
            mailSender.send(message);
            System.out.println("Email de recuperação enviado com sucesso para: " + email);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new RuntimeException("Erro ao enviar email de recuperação de senha");
        }
    }
}
