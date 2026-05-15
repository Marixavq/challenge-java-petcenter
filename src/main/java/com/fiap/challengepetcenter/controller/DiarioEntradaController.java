package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.DiarioEntradaRequestDTO;
import com.fiap.challengepetcenter.DTO.DiarioEntradaResponseDTO;
import com.fiap.challengepetcenter.DTO.PetRequestDTO;
import com.fiap.challengepetcenter.DTO.PetResponseDTO;
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
@RequestMapping("/api/diarioentradas")
public class DiarioEntradaController {

    @Autowired
    private DiarioEntradaService diarioEntradaService;

    @PostMapping
    public ResponseEntity<DiarioEntradaResponseDTO> criar(@Valid @RequestBody DiarioEntradaRequestDTO requestDTO) {
        DiarioEntradaResponseDTO novoDiarioEntrada = diarioEntradaService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoDiarioEntrada);
    }

    @GetMapping
    public ResponseEntity<List<DiarioEntradaResponseDTO>> listarTodos() {
        List<DiarioEntradaResponseDTO> entradas = diarioEntradaService.listarTodos();
        return ResponseEntity.ok(entradas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiarioEntradaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(diarioEntradaService.buscarPorId(id));
    }

    // GET http://localhost:8080/api/diarioentradas/data?data=2026-05-11
    @GetMapping("/data")
    public ResponseEntity<List<DiarioEntradaResponseDTO>> buscarPorData(@RequestParam LocalDate data) {
        return ResponseEntity.ok(diarioEntradaService.buscarPorData(data));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiarioEntradaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DiarioEntradaRequestDTO requestDTO) {
        DiarioEntradaResponseDTO diarioEntradaAtualizado = diarioEntradaService.atualizar(id, requestDTO);
        return ResponseEntity.ok(diarioEntradaAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        diarioEntradaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
