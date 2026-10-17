package com.denkitronik.clienteservice2.cliente.services;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IClienteService {

    // Métodos originales (el findAll() viejo se elimina)
    Cliente findById(Long id);
    Cliente save(Cliente cliente);
    void delete(Long id);
    List<Region> findAllRegiones();

    // NUEVO MÉTODO PARA PAGINACIÓN
    Page<Cliente> findAll(Pageable pageable);
}