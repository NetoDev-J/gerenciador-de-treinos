package com.neto.treinos_api.controller;

import com.neto.treinos_api.dto.SessaoTreinoAtualizacaoRequest;
import com.neto.treinos_api.dto.SessaoTreinoRequest;
import com.neto.treinos_api.dto.SessaoTreinoResponse;
import com.neto.treinos_api.service.SessaoTreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "Sessões", description = "Treinos realizados, um por dia de treino em cada data")
@RestController
@RequestMapping("/sessoes")
@RequiredArgsConstructor
public class SessaoTreinoController {

    private final SessaoTreinoService service;

    @Operation(summary = "Registra um treino realizado")
    @ApiResponse(responseCode = "201", description = "Sessão criada")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (inclui data futura)")
    @ApiResponse(responseCode = "404", description = "Dia de treino não existe")
    @ApiResponse(responseCode = "409", description = "Já existe sessão desse dia de treino nessa data")
    @PostMapping
    public ResponseEntity<SessaoTreinoResponse> criar(@Valid @RequestBody SessaoTreinoRequest request) {
        SessaoTreinoResponse criada = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @Operation(summary = "Lista sessões (mais recentes primeiro), com filtros opcionais por dia de treino e período")
    @ApiResponse(responseCode = "400", description = "Período inválido")
    @GetMapping
    public ResponseEntity<List<SessaoTreinoResponse>> listar(
            @RequestParam(required = false) Long diaTreinoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return ResponseEntity.ok(service.listar(diaTreinoId, dataInicio, dataFim));
    }

    @Operation(summary = "Busca uma sessão pelo id")
    @ApiResponse(responseCode = "404", description = "Sessão não existe")
    @GetMapping("/{id}")
    public ResponseEntity<SessaoTreinoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @Operation(summary = "Atualiza a data e as observações de uma sessão")
    @ApiResponse(responseCode = "400", description = "Dados inválidos (inclui data futura)")
    @ApiResponse(responseCode = "404", description = "Sessão não existe")
    @ApiResponse(responseCode = "409", description = "Já existe sessão desse dia de treino nessa data")
    @PutMapping("/{id}")
    public ResponseEntity<SessaoTreinoResponse> atualizar(@PathVariable Long id,
                                                          @Valid @RequestBody SessaoTreinoAtualizacaoRequest request) {
        return ResponseEntity.ok(service.atualizar(id, request));
    }

    @Operation(summary = "Exclui uma sessão e todas as suas séries")
    @ApiResponse(responseCode = "204", description = "Sessão excluída")
    @ApiResponse(responseCode = "404", description = "Sessão não existe")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
