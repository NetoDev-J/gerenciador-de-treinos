package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.Divisao;

public record DivisaoResponse(Long id, String nome, boolean ativa) {

    public static DivisaoResponse from(Divisao d) {
        return new DivisaoResponse(d.getId(), d.getNome(), d.isAtiva());
    }
}
