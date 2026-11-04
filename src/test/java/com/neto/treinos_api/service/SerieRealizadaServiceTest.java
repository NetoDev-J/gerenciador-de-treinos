package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.*;
import com.neto.treinos_api.exception.*;
import com.neto.treinos_api.model.*;
import com.neto.treinos_api.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static com.neto.treinos_api.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SerieRealizadaServiceTest {

    @Mock SerieRealizadaRepository repository;
    @Mock SessaoTreinoRepository sessaoRepository;
    @Mock ExercicioRepository exercicioRepository;
    @Mock ItemDiaTreinoRepository itemRepository;
    @InjectMocks SerieRealizadaService service;

    private SessaoTreino sessaoPadrao() {
        DiaTreino dia = dia(7L, divisao(1L, "ABC"), DiaSemana.SEGUNDA);
        return sessao(1L, dia, LocalDate.of(2026, 11, 3));
    }

    private SerieRealizadaRequest request(Integer numero) {
        return new SerieRealizadaRequest(5L, numero, 10, new BigDecimal("60.0"));
    }

    @Test
    void criarComExercicioForaDoTreinoDaSessaoLancaConflito() {
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessaoPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(itemRepository.existsByDiaTreinoIdAndExercicioId(7L, 5L)).thenReturn(false);

        assertThatThrownBy(() -> service.criar(1L, request(null)))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void criarSemNumeroUsaAProximaSerie() {
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessaoPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(itemRepository.existsByDiaTreinoIdAndExercicioId(7L, 5L)).thenReturn(true);
        when(repository.maiorNumeroSerie(1L, 5L)).thenReturn(2);
        when(repository.existsBySessaoIdAndExercicioIdAndNumeroSerie(1L, 5L, 3)).thenReturn(false);
        when(repository.save(any(SerieRealizada.class))).thenAnswer(inv -> {
            SerieRealizada s = inv.getArgument(0);
            s.setId(10L);
            return s;
        });

        SerieRealizadaResponse resposta = service.criar(1L, request(null));

        assertThat(resposta.numeroSerie()).isEqualTo(3);
        assertThat(resposta.carga()).isEqualByComparingTo("60.0");
    }

    @Test
    void criarComNumeroJaRegistradoLancaConflito() {
        when(sessaoRepository.findById(1L)).thenReturn(Optional.of(sessaoPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(itemRepository.existsByDiaTreinoIdAndExercicioId(7L, 5L)).thenReturn(true);
        when(repository.existsBySessaoIdAndExercicioIdAndNumeroSerie(1L, 5L, 1)).thenReturn(true);

        assertThatThrownBy(() -> service.criar(1L, request(1)))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void atualizarMantendoOExercicioNaoRevalidaOPertencimentoAoDia() {
        SerieRealizada existente = new SerieRealizada();
        existente.setId(10L);
        existente.setSessao(sessaoPadrao());
        existente.setExercicio(exercicio(5L, "Supino"));
        existente.setNumeroSerie(2);
        existente.setRepeticoes(8);
        existente.setCarga(new BigDecimal("50.0"));

        when(repository.findById(10L)).thenReturn(Optional.of(existente));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(repository.save(existente)).thenReturn(existente);

        SerieRealizadaResponse resposta = service.atualizar(10L, request(null));

        assertThat(resposta.numeroSerie()).isEqualTo(2);
        assertThat(resposta.carga()).isEqualByComparingTo("60.0");
        verify(itemRepository, never()).existsByDiaTreinoIdAndExercicioId(any(), any());
    }
}
