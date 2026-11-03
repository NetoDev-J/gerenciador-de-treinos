package com.neto.treinos_api.repository;

import com.neto.treinos_api.model.Divisao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DivisaoRepository extends JpaRepository<Divisao, Long> {

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Divisao> findByNomeContainingIgnoreCaseOrderByNome(String nome);

    List<Divisao> findByNomeContainingIgnoreCaseAndAtivaOrderByNome(String nome, boolean ativa);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Divisao d SET d.ativa = false WHERE d.ativa = true")
    void desativarTodas();
}