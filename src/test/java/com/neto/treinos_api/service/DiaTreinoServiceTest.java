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

import java.util.List;
import java.util.Optional;

import static com.neto.treinos_api.TestData.dia;
import static com.neto.treinos_api.TestData.divisao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaTreinoServiceTest {

    @Mock DiaTreinoRepository repository;
    @Mock DivisaoRepository divisaoRepository;
    @Mock SessaoTreinoRepository sessaoRepository;
    @InjectMocks DiaTreinoService service;

    private DiaTreinoRequest request(DiaSemana diaSemana) {
        return new DiaTreinoRequest("Treino A", "Peito e Tríceps", diaSemana);
    }

    @Test
    void criarEmDiaDaSemanaOcupadoLancaConflito() {
        when(divisaoRepository.findById(1L)).thenReturn(Optional.of(divisao(1L, "ABC")));
        when(repository.existsByDivisaoIdAndDiaSemana(1L, DiaSemana.SEGUNDA)).thenReturn(true);

        assertThatThrownBy(() -> service.criar(1L, request(DiaSemana.SEGUNDA)))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void criarEmDivisaoInexistenteLancaNaoEncontrado() {
        when(divisaoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(99L, request(DiaSemana.SEGUNDA)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void criarSalvaDiaVinculadoADivisao() {
        when(divisaoRepository.findById(1L)).thenReturn(Optional.of(divisao(1L, "ABC")));
        when(repository.existsByDivisaoIdAndDiaSemana(1L, DiaSemana.QUARTA)).thenReturn(false);
        when(repository.save(any(DiaTreino.class))).thenAnswer(inv -> {
            DiaTreino d = inv.getArgument(0);
            d.setId(10L);
            return d;
        });

        DiaTreinoResponse resposta = service.criar(1L, request(DiaSemana.QUARTA));

        assertThat(resposta.divisaoId()).isEqualTo(1L);
        assertThat(resposta.diaSemana()).isEqualTo(DiaSemana.QUARTA);
    }

    @Test
    void listarOrdenaDeSegundaADomingo() {
        Divisao abc = divisao(1L, "ABC");
        when(divisaoRepository.existsById(1L)).thenReturn(true);
        when(repository.findByDivisaoId(1L)).thenReturn(List.of(
                dia(3L, abc, DiaSemana.SEXTA),
                dia(1L, abc, DiaSemana.SEGUNDA),
                dia(2L, abc, DiaSemana.QUARTA)));

        List<DiaTreinoResponse> dias = service.listar(1L, null);

        assertThat(dias).extracting(DiaTreinoResponse::diaSemana)
                .containsExactly(DiaSemana.SEGUNDA, DiaSemana.QUARTA, DiaSemana.SEXTA);
    }

    @Test
    void excluirDiaComSessoesLancaConflito() {
        when(repository.findById(3L)).thenReturn(Optional.of(dia(3L, divisao(1L, "ABC"), DiaSemana.SEGUNDA)));
        when(sessaoRepository.existsByDiaTreinoId(3L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(3L)).isInstanceOf(ConflitoException.class);
        verify(repository, never()).delete(any());
    }
}
