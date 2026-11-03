package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.ExercicioRequest;
import com.neto.treinos_api.dto.ExercicioResponse;
import com.neto.treinos_api.service.ExercicioService;
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

@Tag(name = "Exercícios", description = "Cadastro e consulta de exercícios")
@RestController
@RequestMapping("/exercicios")
@RequiredArgsConstructor
public class ExercicioController {

    private final ExercicioService service;

    @Operation(summary = "Cadastra um exercício")
    @ApiResponse(responseCode = "201", description = "Exercício criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Já existe exercício com esse nome")
    @PostMapping
    public ResponseEntity<ExercicioResponse> criar(@Valid @RequestBody ExercicioRequest request) {
        ExercicioResponse criado = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(location).body(criado);
    }

    @Operation(summary = "Lista exercícios, com filtros opcionais por nome e grupo muscular")
    @GetMapping
    public ResponseEntity<List<ExercicioResponse>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String grupoMuscular) {
        return ResponseEntity.ok(service.listar(nome, grupoMuscular));
    }

    @Operation(summary = "Busca um exercício pelo id")
    @ApiResponse(responseCode = "200", description = "Exercício encontrado")
    @ApiResponse(responseCode = "404", description = "Exercício não existe")
    @GetMapping("/{id}")
    public ResponseEntity<ExercicioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Atualiza um exercício")
    @ApiResponse(responseCode = "200", description = "Exercício atualizado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Exercício não existe")
    @ApiResponse(responseCode = "409", description = "Já existe exercício com esse nome")
    @PutMapping("/{id}")
    public ResponseEntity<ExercicioResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody ExercicioRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui um exercício")
    @ApiResponse(responseCode = "204", description = "Exercício excluído")
    @ApiResponse(responseCode = "404", description = "Exercício não existe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
