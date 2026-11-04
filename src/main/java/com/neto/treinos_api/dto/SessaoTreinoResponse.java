package com.neto.treinos_api.dto;

import com.neto.treinos_api.model.SessaoTreino;

import java.time.LocalDate;

public record SessaoTreinoResponse(Long id, Long diaTreinoId, String diaTreinoNome,
                                   LocalDate data, String observacoes) {

    public static SessaoTreinoResponse from(SessaoTreino s) {
        return new SessaoTreinoResponse(s.getId(), s.getDiaTreino().getId(), s.getDiaTreino().getNome(),
                s.getData(), s.getObservacoes());
    }
}
