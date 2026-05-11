package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.model.Registro;
import com.fiap.challengepetcenter.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/registros/")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @PostMapping
    public ResponseEntity<Registro> criar(@Valid @RequestBody Registro registro) {
        Registro novoDiarioEntrada = registroService.salvar(registro);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDiarioEntrada);
    }

    @GetMapping
    public ResponseEntity<List<Registro>> listarTodos() {
        return ResponseEntity.ok(registroService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Registro> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(registroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Registro> atualizar(@PathVariable Long id,
                                              @Valid @RequestBody Registro registroAtualizado) {
        return ResponseEntity.ok(registroService.atualizar(id, registroAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        registroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
