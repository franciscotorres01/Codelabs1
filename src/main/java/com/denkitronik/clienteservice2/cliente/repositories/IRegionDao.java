package com.denkitronik.clienteservice2.cliente.repositories;

import com.denkitronik.clienteservice2.cliente.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRegionDao extends JpaRepository<Region, Long> {
}