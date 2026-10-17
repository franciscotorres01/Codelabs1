package com.denkitronik.clienteservice2.cliente.exceptions;

public class ClienteExistsException extends RuntimeException {
    public ClienteExistsException(String message) {
        super(message);
    }
}