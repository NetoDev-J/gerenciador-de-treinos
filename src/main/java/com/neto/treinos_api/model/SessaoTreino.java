package com.neto.treinos_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sessao_treino")
@Getter
@Setter
@NoArgsConstructor
public class SessaoTreino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dia_treino_id", nullable = false)
    private DiaTreino diaTreino;

    @Column(name = "data_treino", nullable = false)
    private LocalDate data;

    @Column(length = 500)
    private String observacoes;

    @OneToMany(mappedBy = "sessao", cascade = CascadeType.REMOVE)
    private List<SerieRealizada> series = new ArrayList<>();
}
