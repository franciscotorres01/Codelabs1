package com.denkitronik.clienteservice2.cliente.controllers;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.services.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@CrossOrigin(origins = "*") // Permite cualquier origen para la plataforma
@RequestMapping("/api/${api.version}/cliente-service")
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    @GetMapping("/clientes")
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.findById(id));
    }

    @PostMapping("/clientes")
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteService.save(cliente));
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@Valid @RequestBody Cliente cliente, @PathVariable Long id) {
        Cliente actual = clienteService.findById(id);
        actual.setNombre(cliente.getNombre());
        actual.setApellido(cliente.getApellido());
        actual.setEmail(cliente.getEmail());
        actual.setRegion(cliente.getRegion());
        return ResponseEntity.ok(clienteService.save(actual));
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}