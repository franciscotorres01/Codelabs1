package com.denkitronik.clienteservice2.cliente.repositories;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
public class ClienteRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("curso_springboot_test")
            .withUsername("postgres")
            .withPassword("12345");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private IClienteDao clienteDao;

    // Test 1: Verificar query personalizada findAllRegiones
    @Test
    public void testFindAllRegiones() {
        List<Region> regiones = clienteDao.findAllRegiones();
        assertNotNull(regiones, "La lista de regiones no debería ser nula");
        assertEquals(8, regiones.size(), "Deberían existir 8 regiones");
    }

    // Test 2: Guardar un cliente
    @Test
    public void testGuardarCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@test.com");

        Cliente guardado = clienteDao.save(cliente);

        assertNotNull(guardado.getId(), "El cliente debería tener ID asignado");
        assertEquals("Linus", guardado.getNombre());
        assertEquals("linus@test.com", guardado.getEmail());
    }

    // Test 3: Buscar un cliente por ID
    @Test
    public void testFindById() {
        Cliente cliente = new Cliente();
        cliente.setNombre("James");
        cliente.setApellido("Gosling");
        cliente.setEmail("james@test.com");
        Cliente guardado = clienteDao.save(cliente);

        Optional<Cliente> encontrado = clienteDao.findById(guardado.getId());

        assertTrue(encontrado.isPresent(), "El cliente debería existir");
        assertEquals("James", encontrado.get().getNombre());
    }

    // Test 4: Eliminar un cliente y verificar directamente en BD
    @Test
    public void testDeleteCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Prueba");
        cliente.setApellido("Delete");
        cliente.setEmail("delete@test.com");
        Cliente guardado = clienteDao.save(cliente);
        Long id = guardado.getId();

        clienteDao.deleteById(id);

        // Verificación directa: volvemos a consultar la BD
        Optional<Cliente> eliminado = clienteDao.findById(id);
        assertFalse(eliminado.isPresent(), "El cliente ya no debería existir en la BD");
    }
}