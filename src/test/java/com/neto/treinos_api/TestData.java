package com.neto.treinos_api;

import com.neto.treinos_api.model.*;

import java.time.LocalDate;

public final class TestData {

    private TestData() {}

    public static Exercicio exercicio(Long id, String nome) {
        Exercicio e = new Exercicio();
        e.setId(id);
        e.setNome(nome);
        e.setGrupoMuscular("Peito");
        return e;
    }

    public static Divisao divisao(Long id, String nome) {
        Divisao d = new Divisao();
        d.setId(id);
        d.setNome(nome);
        return d;
    }

    public static DiaTreino dia(Long id, Divisao divisao, DiaSemana diaSemana) {
        DiaTreino d = new DiaTreino();
        d.setId(id);
        d.setDivisao(divisao);
        d.setNome("Treino " + id);
        d.setGrupoMuscular("Peito");
        d.setDiaSemana(diaSemana);
        return d;
    }

    public static SessaoTreino sessao(Long id, DiaTreino dia, LocalDate data) {
        SessaoTreino s = new SessaoTreino();
        s.setId(id);
        s.setDiaTreino(dia);
        s.setData(data);
        return s;
    }
}
