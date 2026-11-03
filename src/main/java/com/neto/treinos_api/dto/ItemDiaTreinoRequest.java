package com.neto.treinos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ItemDiaTreinoRequest(

        @Schema(description = "Id do exercício", example = "1")
        @NotNull(message = "O exercício é obrigatório")
        Long exercicioId,

        @Schema(description = "Número de séries", example = "4")
        @NotNull(message = "O número de séries é obrigatório")
        @Min(value = 1, message = "O número de séries deve ser maior que zero")
        Integer series,

        @Schema(description = "Repetições por série", example = "10")
        @NotNull(message = "O número de repetições é obrigatório")
        @Min(value = 1, message = "O número de repetições deve ser maior que zero")
        Integer repeticoes,

        @Schema(description = "Descanso entre séries, em segundos (10 a 600)", example = "90")
        @NotNull(message = "O tempo de descanso é obrigatório")
        @Min(value = 10, message = "O descanso deve ser de no mínimo 10 segundos")
        @Max(value = 600, message = "O descanso deve ser de no máximo 600 segundos")
        Integer descansoSegundos,

        @Schema(description = "Posição do exercício no treino; se omitida, vai para o final", example = "1")
        @Min(value = 1, message = "A ordem deve ser maior que zero")
        Integer ordem
) {}
