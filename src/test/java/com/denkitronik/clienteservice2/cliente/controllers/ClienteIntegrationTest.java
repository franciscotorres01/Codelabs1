package com.denkitronik.clienteservice2.cliente.controllers;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClienteIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Forzamos la creación del esquema y la carga de import.sql
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/1/cliente-service/clientes";
    }

    // Variable compartida entre tests (porque usamos PER_CLASS)
    private static Long clienteId;

    // ============ Test 1: POST - Crear un cliente ============
    @Test
    @Order(1)
    @DisplayName("1. POST - Crear un cliente")
    public void test1_crearCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@integration.com");
        cliente.setRegion(new com.denkitronik.clienteservice2.cliente.entities.Region());
        // La región 1 debe existir por import.sql
        com.denkitronik.clienteservice2.cliente.entities.Region region = new com.denkitronik.clienteservice2.cliente.entities.Region();
        region.setId(1L);
        cliente.setRegion(region);

        ResponseEntity<Cliente> response = restTemplate.postForEntity(
                baseUrl(), cliente, Cliente.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode(),
                "Debería devolver 201 Created");
        assertNotNull(response.getBody(), "El cuerpo no debería ser nulo");
        assertEquals("Linus", response.getBody().getNombre());
    }

    // ============ Test 2: GET - Buscar el último cliente creado ============
    @Test
    @Order(2)
    @DisplayName("2. GET - Buscar cliente por ID")
    public void test2_buscarCliente() {
        // Primero obtenemos la lista para saber el ID
        ResponseEntity<Cliente[]> response = restTemplate.getForEntity(
                baseUrl(), Cliente[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Como el endpoint devuelve Page<Cliente>, usamos String para parsear
        ResponseEntity<String> rawResponse = restTemplate.getForEntity(
                baseUrl(), String.class);
        assertTrue(rawResponse.getBody().contains("Linus"),
                "La respuesta debería contener a Linus");
    }

    // ============ Test 3: LIST - Listar todos los clientes ============
    @Test
    @Order(3)
    @DisplayName("3. LIST - Listar todos los clientes")
    public void test3_listarClientes() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl(), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // Verificamos que la respuesta tiene la estructura paginada
        assertTrue(response.getBody().contains("\"content\""),
                "La respuesta debería tener un campo 'content'");
        assertTrue(response.getBody().contains("\"totalElements\""),
                "La respuesta debería tener un campo 'totalElements'");
    }

    // ============ Test 4: PUT - Actualizar un cliente ============
    @Test
    @Order(4)
    @DisplayName("4. PUT - Actualizar cliente existente")
    public void test4_actualizarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus.updated@integration.com");
        com.denkitronik.clienteservice2.cliente.entities.Region region = new com.denkitronik.clienteservice2.cliente.entities.Region();
        region.setId(1L);
        cliente.setRegion(region);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Cliente> request = new HttpEntity<>(cliente, headers);

        // Buscamos un cliente existente (asumimos que el ID 2 existe porque import.sql inserta 12)
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/2", HttpMethod.PUT, request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(),
                "Debería devolver 200 OK");
    }

    // ============ Test 5: GET - 404 si no existe ============
    @Test
    @Order(5)
    @DisplayName("5. GET - 404 cuando el cliente no existe")
    public void test5_buscarClienteNoExiste() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/99999", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Debería devolver 404 Not Found");
        assertTrue(response.getBody().contains("\"status\":404"),
                "El JSON debería contener status 404");
    }

    // ============ Test 6: DELETE - Eliminar un cliente ============
    @Test
    @Order(6)
    @DisplayName("6. DELETE - Eliminar cliente existente")
    public void test6_eliminarCliente() {
        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl() + "/2", HttpMethod.DELETE, null, String.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
                "Debería devolver 204 No Content");
    }

    // ============ Test 7: GET - Verificar que ya no existe (404) ============
    @Test
    @Order(7)
    @DisplayName("7. GET - Verificar que el cliente eliminado ya no existe")
    public void test7_verificarEliminado() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                baseUrl() + "/2", String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
                "Debería devolver 404 porque el cliente fue eliminado");
    }
}