package com.denkitronik.clienteservice2.cliente.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HolaMundoController {

    @GetMapping("/hola")
    public String hola() {
        return "Hola Mundo";
    }
}