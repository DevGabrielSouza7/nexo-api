package com.nexo.api.repository;

import com.nexo.api.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    // Esta interface herda todos os métodos CRUD básicos (salvar, buscar, etc.) automaticamente
}