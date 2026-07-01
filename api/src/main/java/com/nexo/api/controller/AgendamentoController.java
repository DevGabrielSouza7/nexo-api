package com.nexo.api.controller;

import com.nexo.api.model.Agendamento;
import com.nexo.api.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/agendamentos")
@CrossOrigin(origins = "*") // Permite que o formulário HTML envie os dados sem bloqueios de segurança
public class AgendamentoController {

    @Autowired
    private AgendamentoRepository repository;

    @PostMapping
    public Agendamento salvarAgendamento(@RequestBody Agendamento agendamento) {
        // Recebe o JSON do site e grava diretamente no PostgreSQL
        return repository.save(agendamento);
    }
}