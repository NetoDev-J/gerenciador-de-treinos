package com.neto.treinos_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "serie_realizada")
@Getter
@Setter
@NoArgsConstructor
public class SerieRealizada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoTreino sessao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;

    @Column(name = "numero_serie", nullable = false)
    private int numeroSerie;

    @Column(nullable = false)
    private int repeticoes;

    @Column(nullable = false, precision = 6, scale = 2)
    private BigDecimal carga;
}
