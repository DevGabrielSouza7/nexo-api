package com.nexo.api.service;

import com.nexo.api.model.Agendamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Pega automaticamente o e-mail que você configurou no application.properties
    @Value("${spring.mail.username}")
    private String emailRemetente;

    public void enviarEmailsAgendamento(Agendamento agendamento) {
        try {
            // ==========================================================
            // 1. E-MAIL PARA A CLÍNICA (Alerta de novo paciente)
            // ==========================================================
            SimpleMailMessage msgClinica = new SimpleMailMessage();
            msgClinica.setFrom(emailRemetente);
            msgClinica.setTo(emailRemetente); // Manda para o próprio e-mail da clínica
            msgClinica.setSubject("📌 Novo Agendamento: " + agendamento.getNome());
            msgClinica.setText(
                "Um novo agendamento foi recebido pelo site!\n\n" +
                "👤 Nome: " + agendamento.getNome() + "\n" +
                "📱 WhatsApp: " + agendamento.getWhatsapp() + "\n" +
                "📧 E-mail: " + agendamento.getEmail() + "\n" +
                "🧠 Atendimento: " + agendamento.getAtendimento() + "\n" +
                "💻 Modalidade: " + agendamento.getModalidade() + "\n" +
                "💬 Mensagem: " + (agendamento.getMensagem() != null ? agendamento.getMensagem() : "Nenhuma mensagem enviada.") + "\n\n" +
                "Acesse o painel ou entre em contato pelo WhatsApp do paciente para confirmar."
            );
            mailSender.send(msgClinica);

            // ==========================================================
            // 2. E-MAIL PARA O PACIENTE (Confirmação acolhedora)
            // ==========================================================
            SimpleMailMessage msgPaciente = new SimpleMailMessage();
            msgPaciente.setFrom(emailRemetente);
            msgPaciente.setTo(agendamento.getEmail()); // Manda para o e-mail que o paciente digitou
            msgPaciente.setSubject("✨ Recebemos o seu agendamento | Nexo Psicologia");
            msgPaciente.setText(
                "Olá, " + agendamento.getNome() + "!\n\n" +
                "Recebemos o seu pedido de agendamento para " + agendamento.getAtendimento() + " na modalidade " + agendamento.getModalidade() + ".\n\n" +
                "Ficamos muito felizes pelo seu contato! Tudo será tratado com o máximo de sigilo, respeito e acolhimento.\n\n" +
                "Nossa equipe já foi notificada e em breve entraremos em contato pelo seu WhatsApp (" + agendamento.getWhatsapp() + ") para confirmar o melhor dia e horário para a sua sessão.\n\n" +
                "Um abraço,\n" +
                "Equipe Nexo Psicologia Humanista"
            );
            mailSender.send(msgPaciente);

            System.out.println("✅ E-mails disparados com sucesso!");

        } catch (Exception e) {
            // Boa prática: Se o e-mail falhar, o sistema avisa no log, mas não trava o agendamento!
            System.err.println("⚠️ Erro ao disparar e-mail: " + e.getMessage());
        }
    }
}
