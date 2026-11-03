package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.DiaTreinoRequest;
import com.neto.treinos_api.dto.DiaTreinoResponse;
import com.neto.treinos_api.model.DiaSemana;
import com.neto.treinos_api.service.DiaTreinoService;
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

@Tag(name = "Dias de treino", description = "Treino de cada dia da semana dentro de uma divisão")
@RestController
@RequiredArgsConstructor
public class DiaTreinoController {

    private final DiaTreinoService service;

    @Operation(summary = "Cadastra um dia de treino em uma divisão")
    @ApiResponse(responseCode = "201", description = "Dia de treino criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @ApiResponse(responseCode = "409", description = "A divisão já tem treino nesse dia da semana")
    @PostMapping("/divisoes/{divisaoId}/dias")
    public ResponseEntity<DiaTreinoResponse> criar(@PathVariable Long divisaoId,
                                                   @Valid @RequestBody DiaTreinoRequest request) {
        DiaTreinoResponse criado = service.criar(divisaoId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/dias/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @Operation(summary = "Lista os dias de uma divisão (de segunda a domingo), com filtro opcional por dia da semana")
    @ApiResponse(responseCode = "404", description = "Divisão não existe")
    @GetMapping("/divisoes/{divisaoId}/dias")
    public ResponseEntity<List<DiaTreinoResponse>> listar(@PathVariable Long divisaoId,
                                                          @RequestParam(required = false) DiaSemana diaSemana) {
        return ResponseEntity.ok(service.listar(divisaoId, diaSemana));
    }

    @Operation(summary = "Busca um dia de treino pelo id")
    @ApiResponse(responseCode = "404", description = "Dia de treino não existe")
    @GetMapping("/dias/{id}")
    public ResponseEntity<DiaTreinoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Atualiza um dia de treino")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Dia de treino não existe")
    @ApiResponse(responseCode = "409", description = "A divisão já tem treino nesse dia da semana")
    @PutMapping("/dias/{id}")
    public ResponseEntity<DiaTreinoResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody DiaTreinoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui um dia de treino")
    @ApiResponse(responseCode = "204", description = "Dia de treino excluído")
    @ApiResponse(responseCode = "404", description = "Dia de treino não existe")
    @DeleteMapping("/dias/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
