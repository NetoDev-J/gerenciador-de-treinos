package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.ItemDiaTreinoRequest;
import com.neto.treinos_api.dto.ItemDiaTreinoResponse;
import com.neto.treinos_api.service.ItemDiaTreinoService;
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

@Tag(name = "Itens do dia", description = "Exercícios de cada dia de treino, com séries, repetições e descanso")
@RestController
@RequiredArgsConstructor
public class ItemDiaTreinoController {

    private final ItemDiaTreinoService service;

    @Operation(summary = "Adiciona um exercício a um dia de treino")
    @ApiResponse(responseCode = "201", description = "Item criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Dia de treino ou exercício não existe")
    @ApiResponse(responseCode = "409", description = "O exercício já está nesse dia")
    @PostMapping("/dias/{diaTreinoId}/itens")
    public ResponseEntity<ItemDiaTreinoResponse> criar(@PathVariable Long diaTreinoId,
                                                       @Valid @RequestBody ItemDiaTreinoRequest request) {
        ItemDiaTreinoResponse criado = service.criar(diaTreinoId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/itens/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @Operation(summary = "Lista os exercícios de um dia de treino, na ordem de execução")
    @ApiResponse(responseCode = "404", description = "Dia de treino não existe")
    @GetMapping("/dias/{diaTreinoId}/itens")
    public ResponseEntity<List<ItemDiaTreinoResponse>> listar(@PathVariable Long diaTreinoId) {
        return ResponseEntity.ok(service.listar(diaTreinoId));
    }

    @Operation(summary = "Busca um item pelo id")
    @ApiResponse(responseCode = "404", description = "Item não existe")
    @GetMapping("/itens/{id}")
    public ResponseEntity<ItemDiaTreinoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Atualiza séries, repetições, descanso, ordem ou o exercício de um item")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Item ou exercício não existe")
    @ApiResponse(responseCode = "409", description = "O exercício já está nesse dia")
    @PutMapping("/itens/{id}")
    public ResponseEntity<ItemDiaTreinoResponse> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody ItemDiaTreinoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Remove um exercício do dia de treino")
    @ApiResponse(responseCode = "204", description = "Item removido")
    @ApiResponse(responseCode = "404", description = "Item não existe")
    @DeleteMapping("/itens/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
