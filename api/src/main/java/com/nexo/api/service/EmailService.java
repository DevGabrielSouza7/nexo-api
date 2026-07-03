package com.nexo.api.service;

import com.nexo.api.model.Agendamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.email.remetente}")
    private String emailRemetente;

    public void enviarEmailsAgendamento(Agendamento agendamento) {
        // 1. E-mail para a CLÍNICA (Aviso interno)
        enviarEmailViaBrevo(
            emailRemetente, // A clínica recebe no próprio e-mail
            "📌 Novo Agendamento: " + agendamento.getNome(),
            "Um novo agendamento foi recebido pelo site!<br><br>" +
            "👤 <b>Nome:</b> " + agendamento.getNome() + "<br>" +
            "📱 <b>WhatsApp:</b> " + agendamento.getWhatsapp() + "<br>" +
            "📧 <b>E-mail:</b> " + agendamento.getEmail() + "<br>" +
            "🧠 <b>Atendimento:</b> " + agendamento.getAtendimento() + "<br>" +
            "💻 <b>Modalidade:</b> " + agendamento.getModalidade() + "<br>" +
            "💬 <b>Mensagem:</b> " + (agendamento.getMensagem() != null ? agendamento.getMensagem() : "Nenhuma mensagem enviada.")
        );

        // 2. E-mail para o PACIENTE (Confirmação)
        enviarEmailViaBrevo(
            agendamento.getEmail(), // Envia para o e-mail preenchido no formulário
            "✨ Recebemos o seu agendamento | Nexo Psicologia",
            "Olá, <b>" + agendamento.getNome() + "</b>!<br><br>" +
            "Recebemos o seu pedido de agendamento para " + agendamento.getAtendimento() + " na modalidade " + agendamento.getModalidade() + ".<br><br>" +
            "Ficamos muito felizes pelo seu contato! Tudo será tratado com o máximo de sigilo, respeito e acolhimento.<br><br>" +
            "Nossa equipe já foi notificada e em breve entraremos em contato pelo seu WhatsApp (" + agendamento.getWhatsapp() + ") para confirmar o melhor dia e horário para a sua sessão.<br><br>" +
            "Um abraço,<br>Equipe Nexo Psicologia Humanista"
        );
    }

    private void enviarEmailViaBrevo(String emailDestino, String assunto, String conteudoHtml) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = "https://api.brevo.com/v3/smtp/email";

            // Montando os cabeçalhos de segurança com a sua chave
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);
            headers.set("accept", "application/json");

            // Montando o "corpo" da requisição no formato que o Brevo exige (JSON)
            Map<String, Object> body = new HashMap<>();
            
            Map<String, String> sender = new HashMap<>();
            sender.put("name", "Nexo Psicologia");
            sender.put("email", emailRemetente);
            body.put("sender", sender);

            Map<String, String> to = new HashMap<>();
            to.put("email", emailDestino);
            body.put("to", List.of(to));

            body.put("subject", assunto);
            body.put("htmlContent", conteudoHtml);

            // Juntando tudo e disparando para a nuvem
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            
            System.out.println("✅ E-mail HTTP disparado com sucesso! Status da API: " + response.getStatusCode());

        } catch (Exception e) {
            System.err.println("⚠️ Erro ao disparar e-mail via Brevo API: " + e.getMessage());
        }
    }
}
