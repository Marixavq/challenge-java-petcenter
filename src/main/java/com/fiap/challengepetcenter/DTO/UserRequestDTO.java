package com.fiap.challengepetcenter.DTO;

import jakarta.validation.constraints.*;

public record UserRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 200)
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        @Size(max = 250)
        String email,

        @NotBlank(message = "Senha obrigatória")
        @Size(min = 6, max = 8, message = "Senha deve ter entre 6 e 8 caracteres")
        String senha,

        @NotBlank(message = "Telefone obrigatório")
        String telefone,

        @NotBlank(message = "Tipo é obrigatório")
        String tipoUsuario

) {
}



