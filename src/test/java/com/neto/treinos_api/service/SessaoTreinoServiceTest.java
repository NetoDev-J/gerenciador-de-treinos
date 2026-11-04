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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.neto.treinos_api.TestData.dia;
import static com.neto.treinos_api.TestData.divisao;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessaoTreinoServiceTest {

    private static final LocalDate DATA = LocalDate.of(2026, 11, 3);

    @Mock SessaoTreinoRepository repository;
    @Mock DiaTreinoRepository diaTreinoRepository;
    @InjectMocks SessaoTreinoService service;

    private DiaTreino diaPadrao() {
        return dia(1L, divisao(1L, "ABC"), DiaSemana.SEGUNDA);
    }

    @Test
    void criarSessaoDuplicadaNaMesmaDataLancaConflito() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(repository.existsByDiaTreinoIdAndData(1L, DATA)).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new SessaoTreinoRequest(1L, DATA, null)))
                .isInstanceOf(ConflitoException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void criarGuardaObservacaoEmBrancoComoNula() {
        when(diaTreinoRepository.findById(1L)).thenReturn(Optional.of(diaPadrao()));
        when(repository.save(any(SessaoTreino.class))).thenAnswer(inv -> {
            SessaoTreino s = inv.getArgument(0);
            s.setId(7L);
            return s;
        });

        SessaoTreinoResponse resposta = service.criar(new SessaoTreinoRequest(1L, DATA, "   "));

        assertThat(resposta.observacoes()).isNull();
        assertThat(resposta.data()).isEqualTo(DATA);
    }

    @Test
    void listarComPeriodoInvertidoLancaRequisicaoInvalida() {
        assertThatThrownBy(() -> service.listar(null, LocalDate.of(2026, 12, 1), LocalDate.of(2026, 11, 1)))
                .isInstanceOf(RequisicaoInvalidaException.class);
        verifyNoInteractions(repository);
    }

    @Test
    void listarSemFiltrosUsaPeriodoAberto() {
        when(repository.findByDataBetweenOrderByDataDescIdDesc(any(), any())).thenReturn(List.of());

        assertThat(service.listar(null, null, null)).isEmpty();
        verify(repository).findByDataBetweenOrderByDataDescIdDesc(any(), any());
    }
}
