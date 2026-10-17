package com.denkitronik.clienteservice2.cliente.controllers;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteExistsException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteNotFoundException;
import com.denkitronik.clienteservice2.cliente.services.IClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ClienteRestController.class)
public class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    // Test 1: GET /clientes devuelve 200 con paginación
    @Test
    public void testListarClientes() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        Page<Cliente> page = new PageImpl<>(Arrays.asList(cliente), PageRequest.of(0, 10), 1);

        when(clienteService.findAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/1/cliente-service/clientes")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nombre").value("Linus"));
    }

    // Test 2: GET /clientes/{id} devuelve 200 si existe
    @Test
    public void testBuscarClientePorIdEncontrado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNombre("Linus");

        when(clienteService.findById(1L)).thenReturn(cliente);

        mockMvc.perform(get("/api/1/cliente-service/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Linus"));
    }

    // Test 3: GET /clientes/{id} devuelve 404 si no existe
    @Test
    public void testBuscarClientePorIdNoEncontrado() throws Exception {
        when(clienteService.findById(999L)).thenThrow(new ClienteNotFoundException("Cliente no encontrado con ID: 999"));

        mockMvc.perform(get("/api/1/cliente-service/clientes/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    // Test 4: POST /clientes devuelve 201 si los datos son válidos
    @Test
    public void testCrearClienteExitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@linux.com");

        Cliente clienteGuardado = new Cliente();
        clienteGuardado.setId(1L);
        clienteGuardado.setNombre("Linus");

        when(clienteService.save(any(Cliente.class))).thenReturn(clienteGuardado);

        mockMvc.perform(post("/api/1/cliente-service/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Linus"));
    }

    // Test 5: POST /clientes devuelve 409 si el email ya existe
    @Test
    public void testCrearClienteEmailDuplicado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@linux.com");

        when(clienteService.save(any(Cliente.class))).thenThrow(new ClienteExistsException("El cliente con el email linus@linux.com ya existe"));

        mockMvc.perform(post("/api/1/cliente-service/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    // Test 6: POST /clientes devuelve 400 si los datos son inválidos
    @Test
    public void testCrearClienteValidacionFallida() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("");
        cliente.setApellido("Torvalds");
        cliente.setEmail("correo-invalido");

        mockMvc.perform(post("/api/1/cliente-service/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    // Test 7: PUT /clientes/{id} devuelve 200 si la actualización es exitosa
    @Test
    public void testActualizarClienteExitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@linux.org");

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setId(1L);
        clienteActualizado.setNombre("Linus");

        when(clienteService.findById(1L)).thenReturn(cliente);
        when(clienteService.save(any(Cliente.class))).thenReturn(clienteActualizado);

        mockMvc.perform(put("/api/1/cliente-service/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk());
    }

    // Test 8: PUT /clientes/{id} devuelve 404 si no existe
    @Test
    public void testActualizarClienteNoEncontrado() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@linux.org");

        when(clienteService.findById(999L)).thenThrow(new ClienteNotFoundException("Cliente no encontrado con ID: 999"));

        mockMvc.perform(put("/api/1/cliente-service/clientes/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isNotFound());
    }

    // Test 9: PUT /clientes/{id} devuelve 400 si los datos son inválidos
    @Test
    public void testActualizarClienteValidacionFallida() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setNombre("");
        cliente.setApellido("Torvalds");
        cliente.setEmail("correo-invalido");

        mockMvc.perform(put("/api/1/cliente-service/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isBadRequest());
    }

    // Test 10: DELETE /clientes/{id} devuelve 204 si se elimina
    @Test
    public void testEliminarClienteExitoso() throws Exception {
        Cliente cliente = new Cliente();
        cliente.setId(1L);

        when(clienteService.findById(1L)).thenReturn(cliente);

        mockMvc.perform(delete("/api/1/cliente-service/clientes/1"))
                .andExpect(status().isNoContent());
    }

    // Test 11: DELETE /clientes/{id} devuelve 404 si no existe
    @Test
    public void testEliminarClienteNoEncontrado() throws Exception {
        when(clienteService.findById(999L)).thenThrow(new ClienteNotFoundException("Cliente no encontrado con ID: 999"));

        mockMvc.perform(delete("/api/1/cliente-service/clientes/999"))
                .andExpect(status().isNotFound());
    }
}