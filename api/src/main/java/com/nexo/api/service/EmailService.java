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
        // 1. E-mail para a CLÍNICA (Aviso interno formatado)
        String htmlClinica = 
            "<div style=\"background-color: #f7f5f0; padding: 40px 0; font-family: Helvetica, Arial, sans-serif;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);\">" +
                    "<div style=\"background-color: #113A23; padding: 30px; text-align: center;\">" +
                        "<h1 style=\"color: #ffffff; font-size: 24px; margin: 0; font-weight: 400; letter-spacing: 1px;\">Novo Agendamento Realizado</h1>" +
                        "<p style=\"color: #d1d8d4; font-size: 14px; margin: 8px 0 0 0;\">Nexo Psicologia Humanista</p>" +
                    "</div>" +
                    "<div style=\"padding: 40px 30px; color: #333333;\">" +
                        "<p style=\"font-size: 16px; line-height: 1.5; margin-top: 0;\">Olá, equipa! Um novo pedido de consulta foi efetuado através do site.</p>" +
                        "<table style=\"width: 100%; border-collapse: collapse; margin-top: 25px;\">" +
                            "<tr><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #113A23; width: 35%;\">Nome:</td><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; color: #555555;\">" + agendamento.getNome() + "</td></tr>" +
                            "<tr><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #113A23;\">WhatsApp:</td><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; color: #555555;\">" + agendamento.getWhatsapp() + "</td></tr>" +
                            "<tr><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #113A23;\">E-mail:</td><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; color: #555555;\">" + agendamento.getEmail() + "</td></tr>" +
                            "<tr><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #113A23;\">Atendimento:</td><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; color: #555555; text-transform: capitalize;\">" + agendamento.getAtendimento() + "</td></tr>" +
                            "<tr><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; font-weight: bold; color: #113A23;\">Modalidade:</td><td style=\"padding: 12px 15px; border-bottom: 1px solid #eeeeee; color: #555555; text-transform: capitalize;\">" + agendamento.getModalidade() + "</td></tr>" +
                            "<tr><td style=\"padding: 12px 15px; font-weight: bold; color: #113A23; vertical-align: top;\">Mensagem:</td><td style=\"padding: 12px 15px; color: #555555; font-style: italic;\">\"" + (agendamento.getMensagem() != null ? agendamento.getMensagem() : "Nenhuma mensagem enviada.") + "\"</td></tr>" +
                        "</table>" +
                    "</div>" +
                    "<div style=\"background-color: #f0ede6; padding: 20px; text-align: center; color: #777777; font-size: 12px;\">" +
                        "<p style=\"margin: 0;\">© 2026 Nexo Psicologia Humanista. Sistema de Gestão.</p>" +
                    "</div>" +
                "</div>" +
            "</div>";

        enviarEmailViaBrevo(
            emailRemetente, 
            "📌 Novo Agendamento: " + agendamento.getNome(), 
            htmlClinica
        );

        // 2. E-mail para o PACIENTE (Confirmação elegante)
        String htmlPaciente = 
            "<div style=\"background-color: #f7f5f0; padding: 40px 0; font-family: Helvetica, Arial, sans-serif;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);\">" +
                    "<div style=\"background-color: #113A23; padding: 35px 20px; text-align: center;\">" +
                        "<h1 style=\"color: #ffffff; font-size: 26px; margin: 0; font-weight: 300; font-style: italic;\">Nexo Psicologia</h1>" +
                        "<p style=\"color: #d1d8d4; font-size: 13px; margin: 8px 0 0 0; letter-spacing: 2px; text-transform: uppercase;\">Lugar de escuta e cuidado</p>" +
                    "</div>" +
                    "<div style=\"padding: 40px 30px; color: #4a4a4a; text-align: left;\">" +
                        "<h2 style=\"color: #113A23; font-size: 20px; margin-top: 0; font-weight: 400;\">Olá, " + agendamento.getNome() + "!</h2>" +
                        "<p style=\"font-size: 16px; line-height: 1.8;\">Recebemos o seu pedido de agendamento com sucesso. Agradecemos a confiança em nosso espaço e no nosso trabalho terapêutico.</p>" +
                        "<p style=\"font-size: 16px; line-height: 1.8;\">Nossa equipe já foi notificada e entrará em contacto através do seu WhatsApp (<strong>" + agendamento.getWhatsapp() + "</strong>) muito em breve para confirmar os detalhes do seu atendimento de <strong>" + agendamento.getAtendimento() + "</strong> na modalidade <strong>" + agendamento.getModalidade() + "</strong>.</p>" +
                        "<div style=\"background-color: #f7f5f0; border-left: 4px solid #5a6659; padding: 20px; margin: 30px 0; border-radius: 0 8px 8px 0;\">" +
                            "<p style=\"margin: 0; font-size: 15px; color: #2c302e; font-style: italic;\">&quot;Acreditamos que é justamente naquilo que nos une que nossa força se encontra. Estamos aqui para caminhar juntos com você.&quot;</p>" +
                        "</div>" +
                        "<p style=\"font-size: 15px; line-height: 1.6; margin-bottom: 0;\">Se precisar de urgência ou quiser falar connosco diretamente, sinta-se à vontade para nos chamar no WhatsApp institucional: <strong>(79) 9983-8695</strong>.</p>" +
                    "</div>" +
                    "<div style=\"background-color: #f0ede6; padding: 25px; text-align: center; color: #777777; font-size: 12px;\">" +
                        "<p style=\"margin: 0 0 5px 0; font-weight: bold; color: #113A23;\">Nexo Psicologia Humanista</p>" +
                        "<p style=\"margin: 0;\">Todos os direitos reservados.</p>" +
                    "</div>" +
                "</div>" +
            "</div>";

        enviarEmailViaBrevo(
            agendamento.getEmail(), 
            "✨ Recebemos o seu agendamento | Nexo Psicologia", 
            htmlPaciente
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
