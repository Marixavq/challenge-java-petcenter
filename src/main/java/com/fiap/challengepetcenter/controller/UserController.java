package com.fiap.challengepetcenter.controller;

import com.fiap.challengepetcenter.DTO.UserRequestDTO;
import com.fiap.challengepetcenter.DTO.UserResponseDTO;
import com.fiap.challengepetcenter.model.User;
import com.fiap.challengepetcenter.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> criar(@Valid @RequestBody UserRequestDTO requestDTO) {
        UserResponseDTO novoUser = userService.salvar(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUser);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> listarTodos() {
        List<UserResponseDTO> users = userService.listarTodos();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.buscarPorId(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> buscarPorEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.buscarPorEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody UserRequestDTO requestDTO) {
        return ResponseEntity.ok(userService.atualizar(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        userService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
