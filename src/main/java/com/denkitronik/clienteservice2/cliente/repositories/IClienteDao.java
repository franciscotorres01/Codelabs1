package com.denkitronik.clienteservice2.cliente.repositories;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface IClienteDao extends JpaRepository<Cliente, Long> {

    // Consulta JPQL personalizada para obtener todas las regiones
    @Query("from Region")
    List<Region> findAllRegiones();

    // 👇 NUEVO: Método para verificar si un email ya existe
    boolean existsByEmail(String email);
}