package com.fiap.challengepetcenter.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@Tag(name = "Home", description = "Endpoint raiz")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Challenge PetCenter API funcionando!";
    }
}