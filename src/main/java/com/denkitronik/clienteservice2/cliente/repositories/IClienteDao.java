package com.denkitronik.clienteservice2.cliente.repositories;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IClienteDao extends JpaRepository<Cliente, Long> {
}