package com.neto.treinos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record SessaoTreinoAtualizacaoRequest(

        @Schema(description = "Data em que o treino foi realizado", example = "2026-11-03")
        @NotNull(message = "A data é obrigatória")
        @PastOrPresent(message = "A data do treino não pode ser futura")
        LocalDate data,

        @Schema(description = "Observações livres", example = "Treino pesado, dormi pouco")
        @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres")
        String observacoes
) {}
