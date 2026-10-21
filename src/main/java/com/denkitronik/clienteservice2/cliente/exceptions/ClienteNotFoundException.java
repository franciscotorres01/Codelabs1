package com.denkitronik.clienteservice2.cliente.exceptions;

public class ClienteNotFoundException extends RuntimeException {
    public ClienteNotFoundException(Long id) {
        super("Cliente con id " + id + " no encontrado");
    }
}