package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.DiaSemana;
import com.neto.treinos_api.model.DiaTreino;

public record DiaTreinoResponse(Long id, Long divisaoId, String nome,
                                String grupoMuscular, DiaSemana diaSemana) {

    public static DiaTreinoResponse from(DiaTreino d) {
        return new DiaTreinoResponse(d.getId(), d.getDivisao().getId(), d.getNome(),
                d.getGrupoMuscular(), d.getDiaSemana());
    }
}
