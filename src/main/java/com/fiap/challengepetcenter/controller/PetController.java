package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.PetRequestDTO;
import com.fiap.challengepetcenter.DTO.PetResponseDTO;
import com.fiap.challengepetcenter.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping
    public ResponseEntity<PetResponseDTO> criar(@Valid @RequestBody PetRequestDTO requestDTO) {
        PetResponseDTO novoPet = petService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @GetMapping
    public ResponseEntity<List<PetResponseDTO>> listarTodos() {
        List<PetResponseDTO> pets = petService.listarTodos();
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PetResponseDTO>> buscarPorUserId(@PathVariable Long userId) {
        List<PetResponseDTO> pets = petService.buscarPorUserId(userId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<PetResponseDTO>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(petService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PetResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PetRequestDTO requestDTO) {
        PetResponseDTO petAtualizado = petService.atualizar(id, requestDTO);
        return ResponseEntity.ok(petAtualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
