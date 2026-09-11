package com.fiap.challengepetcenter.controller;


import com.fiap.challengepetcenter.dto.request.SolicitacaoRequestDTO;
import com.fiap.challengepetcenter.dto.response.SolicitacaoResponseDTO;
import com.fiap.challengepetcenter.service.SolicitacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/solicitacoes")
@Tag(name = "Solicitações", description = "Endpoints para gerenciamento de solicitações")
public class SolicitacaoController {

    @Autowired
    private SolicitacaoService solicitacaoService;

    @PostMapping
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(
            summary = "Criar solicitação",
            description = "Cria uma nova solicitação de atendimento veterinário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Solicitação criada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> criar(
            @Valid @RequestBody SolicitacaoRequestDTO requestDTO) {

        SolicitacaoResponseDTO novaSolicitacao =
                solicitacaoService.salvar(requestDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaSolicitacao);
    }


    @GetMapping
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Listar solicitações",
            description = "Retorna uma lista de solicitações cadastradas."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Lista de solicitações retornada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(
                            implementation = SolicitacaoResponseDTO.class
                    )
            )
    )
    public ResponseEntity<Page<SolicitacaoResponseDTO>> listarTodos(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<SolicitacaoResponseDTO> solicitacoes =
                solicitacaoService.listarTodos(pageable);

        return ResponseEntity.ok(solicitacoes);
    }


    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Buscar solicitação por ID",
            description = "Retorna uma solicitação específica baseada no ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação encontrada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrada"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                solicitacaoService.buscarPorId(id)
        );
    }


    @GetMapping("/veterinario/{veterinarioId}")
    @PreAuthorize("hasRole('VETERINARIO')")
    @Operation(
            summary = "Buscar solicitações por veterinário",
            description = "Retorna as solicitações associadas a um veterinário específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitações encontradas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Veterinário não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorVeterinarioId(
            @PathVariable Long veterinarioId,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<SolicitacaoResponseDTO> solicitacoes =
                solicitacaoService.buscarPorVeterinarioId(
                        veterinarioId,
                        pageable
                );

        return ResponseEntity.ok(solicitacoes);
    }


    @GetMapping("/pet/{petId}")
    @PreAuthorize("hasRole('TUTOR') or hasRole('VETERINARIO')")
    @Operation(
            summary = "Buscar solicitações por pet",
            description = "Retorna as solicitações associadas a um pet específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitações encontradas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Pet não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorPetId(
            @PathVariable Long petId,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<SolicitacaoResponseDTO> solicitacoes =
                solicitacaoService.buscarPorPetId(
                        petId,
                        pageable
                );

        return ResponseEntity.ok(solicitacoes);
    }


    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('TUTOR')")
    @Operation(
            summary = "Buscar solicitações por tutor",
            description = "Retorna as solicitações associadas a um tutor específico."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitações encontradas com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado"
            )
    })
    public ResponseEntity<Page<SolicitacaoResponseDTO>> buscarPorTutorId(
            @PathVariable Long userId,
            @PageableDefault(
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<SolicitacaoResponseDTO> solicitacoes =
                solicitacaoService.buscarPorTutorId(
                        userId,
                        pageable
                );

        return ResponseEntity.ok(solicitacoes);
    }


    @PatchMapping("/{id}/aceitar")
    @PreAuthorize("hasRole('VETERINARIO')")
    @Operation(
            summary = "Aceitar solicitação",
            description = "Aceita uma solicitação de atendimento veterinário e cria o vínculo entre o pet e o veterinário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação aceita com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitação já foi respondida"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> aceitar(
            @PathVariable Long id) {

        SolicitacaoResponseDTO solicitacao =
                solicitacaoService.aceitar(id);

        return ResponseEntity.ok(solicitacao);
    }


    @PatchMapping("/{id}/recusar")
    @PreAuthorize("hasRole('VETERINARIO')")
    @Operation(
            summary = "Recusar solicitação",
            description = "Recusa uma solicitação de atendimento veterinário."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Solicitação recusada com sucesso"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Solicitação não encontrada"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Solicitação já foi respondida"
            )
    })
    public ResponseEntity<SolicitacaoResponseDTO> recusar(
            @PathVariable Long id) {

        SolicitacaoResponseDTO solicitacao =
                solicitacaoService.recusar(id);

        return ResponseEntity.ok(solicitacao);
    }
}
