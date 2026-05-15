package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.*;
import com.fiap.challengepetcenter.service.RegistroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/registros")
public class RegistroController {

    @Autowired
    private RegistroService registroService;

    @PostMapping
    public ResponseEntity<RegistroResponseDTO> criar(@Valid @RequestBody RegistroRequestDTO requestDTO) {
        RegistroResponseDTO novoRegistro = registroService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoRegistro);
    }

    @GetMapping
    public ResponseEntity<List<RegistroResponseDTO>> listarTodos() {
        List<RegistroResponseDTO> registros = registroService.listarTodos();
        return ResponseEntity.ok(registros);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(registroService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RegistroResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody RegistroRequestDTO requestDTO) {
        RegistroResponseDTO registroAtualizado = registroService.atualizar(id, requestDTO);
        return ResponseEntity.ok(registroAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        registroService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
