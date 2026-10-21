// ClienteNotFoundException.java
package com.denkitronik.clienteservice2.cliente.exceptions;
public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long id) { super("Cliente no encontrado con ID: " + id); }
}