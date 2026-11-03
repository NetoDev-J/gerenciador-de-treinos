package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.Exercicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExercicioRepository extends JpaRepository<Exercicio, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Query("""
            SELECT e FROM Exercicio e
            WHERE LOWER(e.nome) LIKE LOWER(CONCAT('%', :nome, '%'))
              AND (:grupo = '' OR LOWER(e.grupoMuscular) = LOWER(:grupo))
            ORDER BY e.nome
            """)
    List<Exercicio> filtrar(@Param("nome") String nome, @Param("grupo") String grupo);
}
