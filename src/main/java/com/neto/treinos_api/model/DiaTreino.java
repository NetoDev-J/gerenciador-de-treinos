package com.neto.treinos_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dia_treino")
@Getter
@Setter
@NoArgsConstructor
public class DiaTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "divisao_id", nullable = false)
    private Divisao divisao;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(name = "grupo_muscular", nullable = false, length = 100)
    private String grupoMuscular;

    @Enumerated(EnumType.STRING)
    @Column(name = "dia_semana", nullable = false, length = 10)
    private DiaSemana diaSemana;
}
