package com.neto.treinos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SerieRealizadaRequest(

        @Schema(description = "Id do exercício executado", example = "1")
        @NotNull(message = "O exercício é obrigatório")
        Long exercicioId,

        @Schema(description = "Número da série; se omitido, usa a próxima da sequência", example = "1")
        @Min(value = 1, message = "O número da série deve ser maior que zero")
        Integer numeroSerie,

        @Schema(description = "Repetições realizadas", example = "10")
        @NotNull(message = "O número de repetições é obrigatório")
        @Min(value = 1, message = "O número de repetições deve ser maior que zero")
        Integer repeticoes,

        @Schema(description = "Carga utilizada, em kg (0 para peso do corpo)", example = "62.5")
        @NotNull(message = "A carga é obrigatória")
        @DecimalMin(value = "0.0", message = "A carga não pode ser negativa")
        @Digits(integer = 4, fraction = 2, message = "A carga aceita até 4 dígitos inteiros e 2 decimais")
        BigDecimal carga
) {}
