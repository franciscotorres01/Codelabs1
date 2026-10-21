package com.denkitronik.clienteservice2.cliente.services;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;

import java.util.List;

public interface IClienteService {

    // Método simple para listar todos los clientes (sin paginación)
    List<Cliente> findAll();

    Cliente findById(Long id);
    Cliente save(Cliente cliente);
    void delete(Long id);
    List<Region> findAllRegiones();
}