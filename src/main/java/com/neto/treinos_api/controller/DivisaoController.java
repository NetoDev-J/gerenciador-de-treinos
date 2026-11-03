package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.DivisaoRequest;
import com.neto.treinos_api.dto.DivisaoResponse;
import com.neto.treinos_api.service.DivisaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Divisões", description = "Divisões de treino (ABC, Push/Pull/Legs...) e escolha da divisão ativa")
@RestController
@RequestMapping("/divisoes")
@RequiredArgsConstructor
public class DivisaoController {

    private final DivisaoService service;

    @Operation(summary = "Cadastra uma divisão (nasce inativa)")
    @ApiResponse(responseCode = "201", description = "Divisão criada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Já existe divisão com esse nome")
    @PostMapping
    public ResponseEntity<DivisaoResponse> criar(@Valid @RequestBody DivisaoRequest request) {
        DivisaoResponse criada = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Lista divisões, com filtros opcionais por nome e por ativa")
    @GetMapping
    public ResponseEntity<List<DivisaoResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Boolean ativa) {
        return ResponseEntity.ok(service.listar(nome, ativa));
    }

    @Operation(summary = "Busca uma divisão pelo id")
    @ApiResponse(responseCode = "200", description = "Divisão encontrada")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @GetMapping("/{id}")
    public ResponseEntity<DivisaoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Renomeia uma divisão")
    @ApiResponse(responseCode = "200", description = "Divisão atualizada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @ApiResponse(responseCode = "409", description = "Já existe divisão com esse nome")
    @PutMapping("/{id}")
    public ResponseEntity<DivisaoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody DivisaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Ativa a divisão da semana (desativa a que estava ativa)")
    @ApiResponse(responseCode = "200", description = "Divisão ativada")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<DivisaoResponse> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @Operation(summary = "Exclui uma divisão")
    @ApiResponse(responseCode = "204", description = "Divisão excluída")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
