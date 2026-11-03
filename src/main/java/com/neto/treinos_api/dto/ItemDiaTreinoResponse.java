package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.ItemDiaTreino;

public record ItemDiaTreinoResponse(Long id, Long diaTreinoId, Long exercicioId, String exercicioNome,
                                    int series, int repeticoes, int descansoSegundos, int ordem) {

    public static ItemDiaTreinoResponse from(ItemDiaTreino i) {
        return new ItemDiaTreinoResponse(i.getId(), i.getDiaTreino().getId(),
                i.getExercicio().getId(), i.getExercicio().getNome(),
                i.getSeries(), i.getRepeticoes(), i.getDescansoSegundos(), i.getOrdem());
    }
}