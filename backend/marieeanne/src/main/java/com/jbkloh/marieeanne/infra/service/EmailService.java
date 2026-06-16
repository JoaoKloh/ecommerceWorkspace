package com.jbkloh.marieeanne.infra.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService{
    @Autowired
    private JavaMailSender javaMailSender;

    public void enviarCodigoOtp(String para, String codigo) throws MessagingException{

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        mimeMessageHelper.setFrom("suporte.klohstore@gmail.com");
        mimeMessageHelper.setTo(para);
        mimeMessageHelper.setSubject("Código de Acesso - Marie e Anne");
        
        String logoUrl ="./uploads/imagem.png";

        String htmlContent = """
                <div style="text-align: center; font-family: sans-serif;">
                    <img src="%s" alt="Logo Marie e Anne" style="width: 150px; margin-bottom: 20px;">
                    <h2>Olá!</h2>
                    <p>Seu código de verificação de acesso é:</p>
                    <h1 style="color: #d4a373; letter-spacing: 5px;">%s</h1>
                    <p style="font-size: 12px; color: #666;">Este código expira em 10 minutos.</p>
                </div>
            """.formatted(logoUrl, codigo);

        mimeMessageHelper.setText(htmlContent, true); // O 'true' confirma que o conteúdo é HTML
        javaMailSender.send(mimeMessage);
    }
}