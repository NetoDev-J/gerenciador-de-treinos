package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.ItemDiaTreino;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemDiaTreinoRepository extends JpaRepository<ItemDiaTreino, Long> {

    @EntityGraph(attributePaths = "exercicio")
    List<ItemDiaTreino> findByDiaTreinoIdOrderByOrdemAscIdAsc(Long diaTreinoId);

    boolean existsByDiaTreinoIdAndExercicioId(Long diaTreinoId, Long exercicioId);

    boolean existsByDiaTreinoIdAndExercicioIdAndIdNot(Long diaTreinoId, Long exercicioId, Long id);

    boolean existsByExercicioId(Long exercicioId);

    @Query("SELECT COALESCE(MAX(i.ordem), 0) FROM ItemDiaTreino i WHERE i.diaTreino.id = :diaTreinoId")
    Integer maiorOrdem(@Param("diaTreinoId") Long diaTreinoId);
}