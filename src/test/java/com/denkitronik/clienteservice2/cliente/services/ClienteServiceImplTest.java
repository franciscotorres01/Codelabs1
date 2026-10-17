package com.denkitronik.clienteservice2.cliente.services;

import com.denkitronik.clienteservice2.cliente.entities.Cliente;
import com.denkitronik.clienteservice2.cliente.entities.Region;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteExistsException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteNotFoundException;
import com.denkitronik.clienteservice2.cliente.exceptions.ClienteServiceException;
import com.denkitronik.clienteservice2.cliente.repositories.IClienteDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceImplTest {

    @Mock
    private IClienteDao clienteDao;

    @InjectMocks
    private ClienteServiceImpl clienteService;

    // Test 1: findAll devuelve una página con clientes
    @Test
    public void testFindAllReturnsPageWithClients() {
        Pageable pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        Page<Cliente> page = new PageImpl<>(Arrays.asList(cliente));
        when(clienteDao.findAll(pageable)).thenReturn(page);

        Page<Cliente> resultado = clienteService.findAll(pageable);

        assertNotNull(resultado);
        assertEquals(1, resultado.getTotalElements());
        verify(clienteDao, times(1)).findAll(pageable);
    }

    // Test 2: findAll devuelve una página vacía
    @Test
    public void testFindAllReturnsEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> page = new PageImpl<>(new ArrayList<>());
        when(clienteDao.findAll(pageable)).thenReturn(page);

        Page<Cliente> resultado = clienteService.findAll(pageable);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // Test 3: findById encuentra el cliente
    @Test
    public void testFindByIdFound() {
        Long id = 1L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombre("Linus");
        when(clienteDao.findById(id)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.findById(id);

        assertNotNull(resultado);
        assertEquals("Linus", resultado.getNombre());
        verify(clienteDao, times(1)).findById(id);
    }

    // Test 4: findById no encuentra y lanza ClienteNotFoundException
    @Test
    public void testFindByIdNotFoundThrowsException() {
        Long id = 999L;
        when(clienteDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class, () -> {
            clienteService.findById(id);
        });
        verify(clienteDao, times(1)).findById(id);
    }

    // Test 5: save guarda un cliente nuevo
    @Test
    public void testSaveNewClient() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setEmail("linus@linux.com");

        when(clienteDao.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(clienteDao.save(cliente)).thenReturn(cliente);

        Cliente resultado = clienteService.save(cliente);

        assertNotNull(resultado);
        assertEquals("Linus", resultado.getNombre());
        verify(clienteDao, times(1)).existsByEmail(cliente.getEmail());
        verify(clienteDao, times(1)).save(cliente);
    }

    // Test 6: save con email existente lanza ClienteExistsException (409)
    @Test
    public void testSaveWithExistingEmailThrowsException() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setEmail("linus@linux.com");

        when(clienteDao.existsByEmail(cliente.getEmail())).thenReturn(true);

        assertThrows(ClienteExistsException.class, () -> {
            clienteService.save(cliente);
        });
        verify(clienteDao, never()).save(cliente);
    }

    // Test 7: save con error inesperado lanza ClienteServiceException (500)
    @Test
    public void testSaveThrowsClienteServiceExceptionOnUnexpectedError() {
        Cliente cliente = new Cliente();
        cliente.setNombre("Linus");
        cliente.setEmail("linus@linux.com");

        when(clienteDao.existsByEmail(cliente.getEmail())).thenReturn(false);
        when(clienteDao.save(cliente)).thenThrow(new RuntimeException("DB error"));

        assertThrows(ClienteServiceException.class, () -> {
            clienteService.save(cliente);
        });
    }

    // Test 8: delete llama al repositorio
    @Test
    public void testDeleteCallsRepository() {
        Long id = 1L;

        clienteService.delete(id);

        verify(clienteDao, times(1)).deleteById(id);
    }

    // Test 9: findAllRegiones devuelve una lista con regiones
    @Test
    public void testFindAllRegionesReturnsList() {
        Region region1 = new Region();
        region1.setNombre("Colombia");
        List<Region> regiones = Arrays.asList(region1);
        when(clienteDao.findAllRegiones()).thenReturn(regiones);

        List<Region> resultado = clienteService.findAllRegiones();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(clienteDao, times(1)).findAllRegiones();
    }

    // Test 10: findAllRegiones devuelve lista vacía
    @Test
    public void testFindAllRegionesReturnsEmptyList() {
        when(clienteDao.findAllRegiones()).thenReturn(new ArrayList<>());

        List<Region> resultado = clienteService.findAllRegiones();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    // Test 11: findById devuelve los datos correctos
    @Test
    public void testFindByIdReturnsCorrectData() {
        Long id = 5L;
        Cliente cliente = new Cliente();
        cliente.setId(id);
        cliente.setNombre("James");
        cliente.setApellido("Gosling");
        when(clienteDao.findById(id)).thenReturn(Optional.of(cliente));

        Cliente resultado = clienteService.findById(id);

        assertEquals("James", resultado.getNombre());
        assertEquals("Gosling", resultado.getApellido());
        assertEquals(id, resultado.getId());
    }
}