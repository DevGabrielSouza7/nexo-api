package com.nexo.api.controller;

import com.nexo.api.model.Agendamento;
import com.nexo.api.repository.AgendamentoRepository;
import com.nexo.api.service.EmailService; // Importamos o nosso novo serviço de e-mail
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*") // Permite que o formulário HTML envie os dados sem bloqueios
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository repository;

    @Autowired
    private EmailService emailService; // Injetamos o carteiro no controller

    @PostMapping
    public Agendamento salvarAgendamento(@RequestBody Agendamento agendamento) {
        // 1. Grava no banco de dados PostgreSQL (Supabase)
        Agendamento agendamentoSalvo = repository.save(agendamento);
        
        // 2. Dispara automaticamente os dois e-mails (Clínica e Paciente)
        emailService.enviarEmailsAgendamento(agendamentoSalvo);
        
        // 3. Retorna a confirmação para o site
        return agendamentoSalvo;
    }
}
