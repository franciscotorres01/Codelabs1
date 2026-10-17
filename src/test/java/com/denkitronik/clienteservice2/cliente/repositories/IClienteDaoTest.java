package com.denkitronik.clienteservice2.cliente.repositories;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IClienteDaoTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private IClienteDao clienteDao;

    // Test 1: Guardar un cliente con save()
    @Test
    public void testSaveCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setApellido("Torvalds");
        cliente.setEmail("linus@test.com");

        Cliente guardado = clienteDao.save(cliente);

        assertNotNull(guardado.getId(), "El cliente debería tener un ID asignado");
        assertEquals("Linus", guardado.getNombre());
        assertEquals("linus@test.com", guardado.getEmail());
    }

    // Test 2: Buscar por ID con findById()
    @Test
    public void testFindById() {
        Cliente cliente = new Cliente();
        cliente.setNombre("James");
        cliente.setApellido("Gosling");
        cliente.setEmail("james@test.com");

        Cliente persistido = em.persistAndFlush(cliente);

        Optional<Cliente> encontrado = clienteDao.findById(persistido.getId());

        assertTrue(encontrado.isPresent(), "El cliente debería existir");
        assertEquals("James", encontrado.get().getNombre());
    }

    // Test 3: Listar todos con findAll() - CORREGIDO (agregado apellido)
    @Test
    public void testFindAll() {
        Cliente c1 = new Cliente();
        c1.setNombre("Cliente1");
        c1.setApellido("Apellido1");
        c1.setEmail("c1@test.com");
        em.persist(c1);

        Cliente c2 = new Cliente();
        c2.setNombre("Cliente2");
        c2.setApellido("Apellido2");
        c2.setEmail("c2@test.com");
        em.persist(c2);

        em.flush();

        List<Cliente> clientes = clienteDao.findAll();

        assertFalse(clientes.isEmpty(), "La lista no debería estar vacía");
        assertTrue(clientes.size() >= 2, "Deberían existir al menos 2 clientes");
    }

    // Test 4: Eliminar un cliente y verificar con em.find tras flush y clear
    @Test
    public void testDeleteCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Delete");
        cliente.setApellido("Test");
        cliente.setEmail("delete@test.com");

        Cliente persistido = em.persistAndFlush(cliente);
        Long id = persistido.getId();

        clienteDao.deleteById(id);
        em.flush();
        em.clear(); // Limpiamos el caché de primer nivel del EntityManager

        Cliente eliminado = em.find(Cliente.class, id);

        assertNull(eliminado, "El cliente debería estar eliminado de la BD");
    }

    // Test 5: Actualizar un cliente - CORREGIDO (agregado apellido)
    @Test
    public void testUpdateCliente() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Original");
        cliente.setApellido("ApellidoOriginal");
        cliente.setEmail("original@test.com");

        Cliente persistido = em.persistAndFlush(cliente);

        persistido.setNombre("Actualizado");
        clienteDao.save(persistido);
        em.flush();
        em.clear();

        Cliente actualizado = em.find(Cliente.class, persistido.getId());

        assertEquals("Actualizado", actualizado.getNombre(), "El nombre debería haber cambiado");
    }

    // Test 6: Query personalizada findAllRegiones()
    @Test
    public void testFindAllRegiones() {
        List<Region> regiones = clienteDao.findAllRegiones();

        assertNotNull(regiones, "La lista de regiones no debería ser nula");
        assertEquals(8, regiones.size(), "Deberían existir 8 regiones del import.sql");
    }
}