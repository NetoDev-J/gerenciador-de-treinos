package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.Exercicio;

public record ExercicioResponse(Long id, String nome, String grupoMuscular) {

    public static ExercicioResponse from(Exercicio e) {
        return new ExercicioResponse(e.getId(), e.getNome(), e.getGrupoMuscular());
    }
}