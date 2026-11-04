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

import java.util.Optional;

import static com.neto.treinos_api.TestData.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemDiaTreinoServiceTest {

    @Mock ItemDiaTreinoRepository repository;
    @Mock DiaTreinoRepository diaTreinoRepository;
    @Mock ExercicioRepository exercicioRepository;
    @InjectMocks ItemDiaTreinoService service;

    private DiaTreino diaPadrao() {
        return dia(1L, divisao(1L, "ABC"), DiaSemana.SEGUNDA);
    }

    private void salvarComId() {
        when(repository.save(any(ItemDiaTreino.class))).thenAnswer(inv -> {
            ItemDiaTreino i = inv.getArgument(0);
            i.setId(10L);
            return i;
        });
    }

    @Test
    void criarExercicioRepetidoNoDiaLancaConflito() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(repository.existsByDiaTreinoIdAndExercicioId(1L, 5L)).thenReturn(true);

        assertThatThrownBy(() -> service.criar(1L, new ItemDiaTreinoRequest(5L, 4, 10, 90, null)))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void criarSemOrdemUsaAProximaDaSequencia() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(repository.existsByDiaTreinoIdAndExercicioId(1L, 5L)).thenReturn(false);
        when(repository.maiorOrdem(1L)).thenReturn(3);
        salvarComId();

        ItemDiaTreinoResponse resposta = service.criar(1L, new ItemDiaTreinoRequest(5L, 4, 10, 90, null));

        assertThat(resposta.ordem()).isEqualTo(4);
        assertThat(resposta.descansoSegundos()).isEqualTo(90);
    }

    @Test
    void criarComOrdemInformadaNaoConsultaAMaiorOrdem() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(exercicioRepository.findById(5L)).thenReturn(Optional.of(exercicio(5L, "Supino")));
        when(repository.existsByDiaTreinoIdAndExercicioId(1L, 5L)).thenReturn(false);
        salvarComId();

        ItemDiaTreinoResponse resposta = service.criar(1L, new ItemDiaTreinoRequest(5L, 4, 10, 90, 2));

        assertThat(resposta.ordem()).isEqualTo(2);
        verify(repository, never()).maiorOrdem(any());
    }

    @Test
    void criarComExercicioInexistenteLancaNaoEncontrado() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(exercicioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(1L, new ItemDiaTreinoRequest(99L, 4, 10, 90, null)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}