package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.model.DiarioEntrada;
import com.fiap.challengepetcenter.service.DiarioEntradaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/diarioentrada/pets")
public class DiarioEntradaController {

    @Autowired
    private DiarioEntradaService diarioEntradaService;

    @PostMapping
    public ResponseEntity<DiarioEntrada> criar(@Valid @RequestBody DiarioEntrada diarioEntrada) {
        DiarioEntrada novoDiarioEntrada = diarioEntradaService.salvar(diarioEntrada);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDiarioEntrada);
    }

    @GetMapping
    public ResponseEntity<List<DiarioEntrada>> listarTodos() {
        return ResponseEntity.ok(diarioEntradaService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiarioEntrada> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diarioEntradaService.buscarPorId(id));
    }

    // GET /diario-entradas/data?data=2026-05-10
    @GetMapping("/data")
    public ResponseEntity<List<DiarioEntrada>> buscarPorData(@RequestParam LocalDate data) {
        return ResponseEntity.ok(diarioEntradaService.buscarPorData(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiarioEntrada> atualizar(@PathVariable Long id,
                                                   @Valid @RequestBody DiarioEntrada diarioEntradaAtualizado) {
        return ResponseEntity.ok(diarioEntradaService.atualizar(id, diarioEntradaAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diarioEntradaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
