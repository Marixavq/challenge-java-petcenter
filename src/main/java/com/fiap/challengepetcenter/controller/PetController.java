package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.model.Pet;
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
    public ResponseEntity<Pet> criar(@Valid @RequestBody Pet pet) {
        Pet novoPet = petService.salvar(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPet);
    }

    @GetMapping
    public ResponseEntity<List<Pet>> listarTodos() {
        return ResponseEntity.ok(petService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(petService.buscarPorId(id));
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<Pet>> buscarPorUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(petService.buscarPorUserId(userId));
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<Pet>> buscarPorNome(@PathVariable String nome) {
        return ResponseEntity.ok(petService.buscarPorNome(nome));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> atualizar(@PathVariable Long id,
                                         @Valid @RequestBody Pet petAtualizado) {
        return ResponseEntity.ok(petService.atualizar(id, petAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        petService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
