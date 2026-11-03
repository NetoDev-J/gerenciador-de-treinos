package com.neto.treinos_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "divisao")
@Getter
@Setter
@NoArgsConstructor
public class Divisao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nome;

    @Column(nullable = false)
    private boolean ativa;

    @OneToMany(mappedBy = "divisao", cascade = CascadeType.REMOVE)
    private List<DiaTreino> dias = new ArrayList<>();
}