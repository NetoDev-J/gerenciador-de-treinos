package com.neto.treinos_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ExercicioRequest(

        @Schema(description = "Nome do exercício", example = "Supino reto")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @Schema(description = "Grupo muscular trabalhado", example = "Peito")
        @NotBlank(message = "O grupo muscular é obrigatório")
        @Size(max = 50, message = "O grupo muscular deve ter no máximo 50 caracteres")
        String grupoMuscular
) {}
