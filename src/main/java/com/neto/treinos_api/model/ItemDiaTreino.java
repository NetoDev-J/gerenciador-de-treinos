package com.neto.treinos_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "item_dia_treino")
@Getter
@Setter
@NoArgsConstructor
public class ItemDiaTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dia_treino_id", nullable = false)
    private DiaTreino diaTreino;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(nullable = false)
    private int series;

    @Column(nullable = false)
    private int repeticoes;

    @Column(name = "descanso_segundos", nullable = false)
    private int descansoSegundos;

    @Column(nullable = false)
    private int ordem;
}
