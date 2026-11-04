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

import static com.neto.treinos_api.TestData.exercicio;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExercicioServiceTest {

    @Mock ExercicioRepository repository;
    @Mock ItemDiaTreinoRepository itemRepository;
    @Mock SerieRealizadaRepository serieRepository;
    @InjectMocks ExercicioService service;

    @Test
    void criarComNomeDuplicadoLancaConflito() {
        when(repository.existsByNomeIgnoreCase("Supino reto")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new ExercicioRequest("Supino reto", "Peito")))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void criarSalvaComNomeSemEspacosNasPontas() {
        when(repository.existsByNomeIgnoreCase("Supino reto")).thenReturn(false);
        when(repository.save(any(Exercicio.class))).thenAnswer(inv -> {
            Exercicio e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        ExercicioResponse resposta = service.criar(new ExercicioRequest("  Supino reto  ", " Peito "));

        assertThat(resposta.id()).isEqualTo(1L);
        assertThat(resposta.nome()).isEqualTo("Supino reto");
        assertThat(resposta.grupoMuscular()).isEqualTo("Peito");
    }

    @Test
    void atualizarParaNomeDeOutroExercicioLancaConflito() {
        when(repository.findById(1L)).thenReturn(Optional.of(exercicio(1L, "Agachamento")));
        when(repository.existsByNomeIgnoreCaseAndIdNot("Supino reto", 1L)).thenReturn(true);

        assertThatThrownBy(() -> service.atualizar(1L, new ExercicioRequest("Supino reto", "Peito")))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void excluirExercicioEmDiaDeTreinoLancaConflito() {
        when(repository.findById(1L)).thenReturn(Optional.of(exercicio(1L, "Supino")));
        when(itemRepository.existsByExercicioId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L)).isInstanceOf(ConflitoException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void excluirExercicioComSeriesLancaConflito() {
        when(repository.findById(1L)).thenReturn(Optional.of(exercicio(1L, "Supino")));
        when(itemRepository.existsByExercicioId(1L)).thenReturn(false);
        when(serieRepository.existsByExercicioId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L)).isInstanceOf(ConflitoException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void excluirExercicioLivreRemove() {
        Exercicio e = exercicio(1L, "Supino");
        when(repository.findById(1L)).thenReturn(Optional.of(e));
        when(itemRepository.existsByExercicioId(1L)).thenReturn(false);
        when(serieRepository.existsByExercicioId(1L)).thenReturn(false);

        service.excluir(1L);

        verify(repository).delete(e);
    }

    @Test
    void buscarInexistenteLancaNaoEncontrado() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscar(99L)).isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
