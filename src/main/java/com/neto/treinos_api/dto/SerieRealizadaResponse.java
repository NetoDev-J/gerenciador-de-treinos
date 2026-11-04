package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.SerieRealizada;

import java.math.BigDecimal;

public record SerieRealizadaResponse(Long id, Long sessaoId, Long exercicioId, String exercicioNome,
                                     int numeroSerie, int repeticoes, BigDecimal carga) {

    public static SerieRealizadaResponse from(SerieRealizada s) {
        return new SerieRealizadaResponse(s.getId(), s.getSessao().getId(),
                s.getExercicio().getId(), s.getExercicio().getNome(),
                s.getNumeroSerie(), s.getRepeticoes(), s.getCarga());
    }
}
