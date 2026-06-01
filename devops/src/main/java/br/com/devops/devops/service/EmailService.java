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
        String linkReset = "http://localhost:8080/redefinir-senha?token=" + token;

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Recuperacao de Senha - Sistema DevOps");
            message.setFrom("seu_email@gmail.com");

            String corpo = "Ola " + nomeUsuario + ",\n\n"
                    + "Voce solicitou uma recuperacao de senha. Clique no link abaixo para redefinir sua senha:\n\n"
                    + linkReset + "\n\n"
                    + "Este link expira em 24 horas.\n\n"
                    + "Se voce nao solicitou isto, ignore este email.\n\n"
                    + "Atenciosamente,\nSistema DevOps";

            message.setText(corpo);
            mailSender.send(message);
            System.out.println("Email de recuperacao enviado com sucesso para: " + email);
        } catch (Exception e) {
            System.err.println("Nao foi possivel enviar o email de recuperacao: " + e.getMessage());
            System.out.println("Link de recuperacao gerado para ambiente local: " + linkReset);
        }
    }
}
