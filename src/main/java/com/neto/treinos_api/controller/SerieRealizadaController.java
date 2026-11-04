package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.SerieRealizadaRequest;
import com.neto.treinos_api.dto.SerieRealizadaResponse;
import com.neto.treinos_api.service.SerieRealizadaService;
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

@Tag(name = "Séries realizadas", description = "Repetições e carga de cada série executada em uma sessão")
@RestController
@RequiredArgsConstructor
public class SerieRealizadaController {

    private final SerieRealizadaService service;

    @Operation(summary = "Registra uma série em uma sessão")
    @ApiResponse(responseCode = "201", description = "Série registrada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Sessão ou exercício não existe")
    @ApiResponse(responseCode = "409", description = "Exercício fora do treino da sessão ou série já registrada")
    @PostMapping("/sessoes/{sessaoId}/series")
    public ResponseEntity<SerieRealizadaResponse> criar(@PathVariable Long sessaoId,
                                                        @Valid @RequestBody SerieRealizadaRequest request) {
        SerieRealizadaResponse criada = service.criar(sessaoId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/series/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Lista as séries de uma sessão, com filtro opcional por exercício")
    @ApiResponse(responseCode = "404", description = "Sessão não existe")
    @GetMapping("/sessoes/{sessaoId}/series")
    public ResponseEntity<List<SerieRealizadaResponse>> listar(@PathVariable Long sessaoId,
                                                               @RequestParam(required = false) Long exercicioId) {
        return ResponseEntity.ok(service.listar(sessaoId, exercicioId));
    }

    @Operation(summary = "Busca uma série pelo id")
    @ApiResponse(responseCode = "404", description = "Série não existe")
    @GetMapping("/series/{id}")
    public ResponseEntity<SerieRealizadaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Atualiza repetições, carga, número ou exercício de uma série")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Série ou exercício não existe")
    @ApiResponse(responseCode = "409", description = "Exercício fora do treino da sessão ou série já registrada")
    @PutMapping("/series/{id}")
    public ResponseEntity<SerieRealizadaResponse> atualizar(@PathVariable Long id,
                                                            @Valid @RequestBody SerieRealizadaRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui uma série")
    @ApiResponse(responseCode = "204", description = "Série excluída")
    @ApiResponse(responseCode = "404", description = "Série não existe")
    @DeleteMapping("/series/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}