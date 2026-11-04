package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.SerieRealizada;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SerieRealizadaRepository extends JpaRepository<SerieRealizada, Long> {

    @EntityGraph(attributePaths = "exercicio")
    List<SerieRealizada> findBySessaoIdOrderByIdAsc(Long sessaoId);

    @EntityGraph(attributePaths = "exercicio")
    List<SerieRealizada> findBySessaoIdAndExercicioIdOrderByNumeroSerieAsc(Long sessaoId, Long exercicioId);

    boolean existsBySessaoIdAndExercicioIdAndNumeroSerie(Long sessaoId, Long exercicioId, int numeroSerie);

    boolean existsBySessaoIdAndExercicioIdAndNumeroSerieAndIdNot(Long sessaoId, Long exercicioId,
                                                                 int numeroSerie, Long id);

    boolean existsByExercicioId(Long exercicioId);

    @Query("""
            SELECT COALESCE(MAX(s.numeroSerie), 0) FROM SerieRealizada s
            WHERE s.sessao.id = :sessaoId AND s.exercicio.id = :exercicioId
            """)
    Integer maiorNumeroSerie(@Param("sessaoId") Long sessaoId, @Param("exercicioId") Long exercicioId);
}
