package com.denkitronik.clienteservice2.cliente.controllers;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import com.denkitronik.clienteservice2.cliente.services.IClienteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = {"http://localhost:4200"})
@RequestMapping("/api/${api.version}/cliente-service")
public class ClienteRestController {

    @Autowired
    private IClienteService clienteService;

    // 1. Listar todas las regiones
    @GetMapping("/clientes/regiones")
    public List<Region> listarRegiones() {
        return clienteService.findAllRegiones();
    }

    // 2. Listar todos los clientes (CON PAGINACIÓN)
    @GetMapping("/clientes")
    public ResponseEntity<Page<Cliente>> listarClientes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientes = clienteService.findAll(pageable);

        return ResponseEntity.ok(clientes);
    }

    // 3. Buscar un cliente por ID (Ya no verificamos null, el servicio lanza la excepción)
    @GetMapping("/clientes/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id);
        return ResponseEntity.ok(cliente);
    }

    // 4. Crear un nuevo cliente
    @PostMapping("/clientes")
    public ResponseEntity<Cliente> crearCliente(@Valid @RequestBody Cliente cliente) {
        Cliente nuevoCliente = clienteService.save(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
    }

    // 5. Actualizar un cliente existente
    @PutMapping("/clientes/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@Valid @RequestBody Cliente cliente, @PathVariable Long id) {
        Cliente clienteActual = clienteService.findById(id); // Lanza excepción 404 si no existe
        clienteActual.setNombre(cliente.getNombre());
        clienteActual.setApellido(cliente.getApellido());
        clienteActual.setEmail(cliente.getEmail());
        clienteActual.setCreateAt(cliente.getCreateAt());
        clienteActual.setRegion(cliente.getRegion());

        Cliente clienteActualizado = clienteService.save(clienteActual);
        return ResponseEntity.ok(clienteActualizado);
    }

    // 6. Eliminar un cliente
    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        Cliente cliente = clienteService.findById(id); // Lanza excepción 404 si no existe
        clienteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}