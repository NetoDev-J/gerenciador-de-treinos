package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.SessaoTreino;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SessaoTreinoRepository extends JpaRepository<SessaoTreino, Long> {

    @EntityGraph(attributePaths = "diaTreino")
    List<SessaoTreino> findByDataBetweenOrderByDataDescIdDesc(LocalDate inicio, LocalDate fim);

    @EntityGraph(attributePaths = "diaTreino")
    List<SessaoTreino> findByDiaTreinoIdAndDataBetweenOrderByDataDescIdDesc(Long diaTreinoId,
                                                                            LocalDate inicio, LocalDate fim);

    boolean existsByDiaTreinoIdAndData(Long diaTreinoId, LocalDate data);

    boolean existsByDiaTreinoIdAndDataAndIdNot(Long diaTreinoId, LocalDate data, Long id);

    boolean existsByDiaTreinoId(Long diaTreinoId);

    boolean existsByDiaTreinoDivisaoId(Long divisaoId);
}
