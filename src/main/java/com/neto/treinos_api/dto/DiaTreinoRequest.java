package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.DiaSemana;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DiaTreinoRequest(

        @Schema(description = "Nome do treino", example = "Treino A")
        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
        String nome,

        @Schema(description = "Grupos musculares do dia", example = "Peito e Tríceps")
        @NotBlank(message = "O grupo muscular é obrigatório")
        @Size(max = 100, message = "O grupo muscular deve ter no máximo 100 caracteres")
        String grupoMuscular,

        @Schema(description = "Dia da semana em que o treino é feito", example = "SEGUNDA")
        @NotNull(message = "O dia da semana é obrigatório")
        DiaSemana diaSemana
) {}
