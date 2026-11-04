package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.*;
import com.neto.treinos_api.exception.*;
import com.neto.treinos_api.model.*;
import com.neto.treinos_api.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.neto.treinos_api.TestData.divisao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DivisaoServiceTest {

    @Mock DivisaoRepository repository;
    @Mock SessaoTreinoRepository sessaoRepository;
    @InjectMocks DivisaoService service;

    @Test
    void criarComNomeDuplicadoLancaConflito() {
        when(repository.existsByNomeIgnoreCase("ABC")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new DivisaoRequest("ABC")))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void divisaoNovaNasceInativa() {
        when(repository.existsByNomeIgnoreCase("ABC")).thenReturn(false);
        when(repository.save(any(Divisao.class))).thenAnswer(inv -> {
            Divisao d = inv.getArgument(0);
            d.setId(1L);
            return d;
        });

        DivisaoResponse resposta = service.criar(new DivisaoRequest("ABC"));

        assertThat(resposta.ativa()).isFalse();
    }

    @Test
    void ativarDesativaTodasAntesDeAtivarAEscolhida() {
        Divisao d = divisao(2L, "PPL");
        when(repository.existsById(2L)).thenReturn(true);
        when(repository.findById(2L)).thenReturn(Optional.of(d));
        when(repository.save(d)).thenReturn(d);

        DivisaoResponse resposta = service.ativar(2L);

        assertThat(resposta.ativa()).isTrue();
        InOrder ordem = inOrder(repository);
        ordem.verify(repository).desativarTodas();
        ordem.verify(repository).save(d);
    }

    @Test
    void ativarInexistenteNaoMexeNasDemais() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.ativar(99L)).isInstanceOf(RecursoNaoEncontradoException.class);
        verify(repository, never()).desativarTodas();
    }

    @Test
    void excluirDivisaoComSessoesLancaConflito() {
        when(repository.findById(1L)).thenReturn(Optional.of(divisao(1L, "ABC")));
        when(sessaoRepository.existsByDiaTreinoDivisaoId(1L)).thenReturn(true);

        assertThatThrownBy(() -> service.excluir(1L)).isInstanceOf(ConflitoException.class);
        verify(repository, never()).delete(any());
    }
}