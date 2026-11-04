package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.SessaoTreinoAtualizacaoRequest;
import com.neto.treinos_api.dto.SessaoTreinoRequest;
import com.neto.treinos_api.dto.SessaoTreinoResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.exception.RequisicaoInvalidaException;
import com.neto.treinos_api.model.DiaTreino;
import com.neto.treinos_api.model.SessaoTreino;
import com.neto.treinos_api.repository.DiaTreinoRepository;
import com.neto.treinos_api.repository.SessaoTreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoTreinoService {

    private static final LocalDate DATA_MINIMA = LocalDate.of(1900, 1, 1);
    private static final LocalDate DATA_MAXIMA = LocalDate.of(2999, 12, 31);

    private final SessaoTreinoRepository repository;
    private final DiaTreinoRepository diaTreinoRepository;

    /** Regra: no máximo uma sessão por dia de treino em cada data. */
    @Transactional
    public SessaoTreinoResponse criar(SessaoTreinoRequest request) {
        DiaTreino dia = diaTreinoRepository.findById(request.diaTreinoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Dia de treino não encontrado: id " + request.diaTreinoId()));

        if (repository.existsByDiaTreinoIdAndData(dia.getId(), request.data())) {
            throw new ConflitoException("Já existe uma sessão do dia '" + dia.getNome()
                    + "' em " + request.data());
        }

        SessaoTreino sessao = new SessaoTreino();
        sessao.setDiaTreino(dia);
        sessao.setData(request.data());
        sessao.setObservacoes(limpar(request.observacoes()));
        return SessaoTreinoResponse.from(repository.save(sessao));
    }

    @Transactional(readOnly = true)
    public List<SessaoTreinoResponse> listar(Long diaTreinoId, LocalDate dataInicio, LocalDate dataFim) {
        LocalDate inicio = dataInicio != null ? dataInicio : DATA_MINIMA;
        LocalDate fim = dataFim != null ? dataFim : DATA_MAXIMA;
        if (inicio.isAfter(fim)) {
            throw new RequisicaoInvalidaException("A data inicial não pode ser posterior à data final");
        }

        List<SessaoTreino> sessoes = (diaTreinoId == null)
                ? repository.findByDataBetweenOrderByDataDescIdDesc(inicio, fim)
                : repository.findByDiaTreinoIdAndDataBetweenOrderByDataDescIdDesc(diaTreinoId, inicio, fim);

        return sessoes.stream().map(SessaoTreinoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SessaoTreinoResponse buscar(Long id) {
        return SessaoTreinoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public SessaoTreinoResponse atualizar(Long id, SessaoTreinoAtualizacaoRequest request) {
        SessaoTreino sessao = buscarEntidade(id);
        DiaTreino dia = sessao.getDiaTreino();

        if (repository.existsByDiaTreinoIdAndDataAndIdNot(dia.getId(), request.data(), id)) {
            throw new ConflitoException("Já existe uma sessão do dia '" + dia.getNome()
                    + "' em " + request.data());
        }

        sessao.setData(request.data());
        sessao.setObservacoes(limpar(request.observacoes()));
        return SessaoTreinoResponse.from(repository.save(sessao));
    }

    @Transactional
    public void excluir(Long id) {
        // As séries da sessão são removidas em cascata.
        repository.delete(buscarEntidade(id));
    }

    private SessaoTreino buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessão não encontrada: id " + id));
    }

    private String limpar(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}