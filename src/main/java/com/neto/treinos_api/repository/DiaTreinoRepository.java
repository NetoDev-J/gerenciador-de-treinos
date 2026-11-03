package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.DiaSemana;
import com.neto.treinos_api.model.DiaTreino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaTreinoRepository extends JpaRepository<DiaTreino, Long> {

    List<DiaTreino> findByDivisaoId(Long divisaoId);

    List<DiaTreino> findByDivisaoIdAndDiaSemana(Long divisaoId, DiaSemana diaSemana);

    boolean existsByDivisaoIdAndDiaSemana(Long divisaoId, DiaSemana diaSemana);

    boolean existsByDivisaoIdAndDiaSemanaAndIdNot(Long divisaoId, DiaSemana diaSemana, Long id);
}
