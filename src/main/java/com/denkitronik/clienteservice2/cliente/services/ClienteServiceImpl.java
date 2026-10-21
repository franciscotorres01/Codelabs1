package com.denkitronik.clienteservice2.cliente.services;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteExistsException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteNotFoundException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteServiceException;
import com.denkitronik.clienteservice2.cliente.repositories.IClienteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {

    @Autowired
    private IClienteDao clienteDao;

    @Override
    @Transactional(readOnly = true)
    public Page<Cliente> findAll(Pageable pageable) {
        return clienteDao.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Cliente findById(Long id) {
        return clienteDao.findById(id).orElseThrow(() -> new ClienteNotFoundException(id));
    }

    @Override
    @Transactional
    public Cliente save(Cliente cliente) {
        if (clienteDao.existsByEmail(cliente.getEmail())) {
            throw new ClienteExistsException("El email " + cliente.getEmail() + " ya está registrado");
        }
        try {
            return clienteDao.save(cliente);
        } catch (Exception e) {
            throw new ClienteServiceException("Error al guardar el cliente");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!clienteDao.existsById(id)) {
            throw new ClienteNotFoundException(id);
        }
        clienteDao.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Region> findAllRegiones() {
        return clienteDao.findAllRegiones();
    }
}