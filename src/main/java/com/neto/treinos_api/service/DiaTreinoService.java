package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.DiaTreinoRequest;
import com.neto.treinos_api.dto.DiaTreinoResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.model.DiaSemana;
import com.neto.treinos_api.model.DiaTreino;
import com.neto.treinos_api.model.Divisao;
import com.neto.treinos_api.repository.DiaTreinoRepository;
import com.neto.treinos_api.repository.DivisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaTreinoService {

    private final DiaTreinoRepository repository;
    private final DivisaoRepository divisaoRepository;

    /** Regra de negócio: no máximo um treino por dia da semana em cada divisão. */
    @Transactional
    public DiaTreinoResponse criar(Long divisaoId, DiaTreinoRequest request) {
        Divisao divisao = divisaoRepository.findById(divisaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Divisão não encontrada: id " + divisaoId));

        if (repository.existsByDivisaoIdAndDiaSemana(divisaoId, request.diaSemana())) {
            throw new ConflitoException("A divisão '" + divisao.getNome()
                    + "' já tem um treino em " + request.diaSemana());
        }

        DiaTreino dia = new DiaTreino();
        dia.setDivisao(divisao);
        dia.setNome(request.nome().trim());
        dia.setGrupoMuscular(request.grupoMuscular().trim());
        dia.setDiaSemana(request.diaSemana());
        return DiaTreinoResponse.from(repository.save(dia));
    }

    @Transactional(readOnly = true)
    public List<DiaTreinoResponse> listar(Long divisaoId, DiaSemana diaSemana) {
        if (!divisaoRepository.existsById(divisaoId)) {
            throw new RecursoNaoEncontradoException("Divisão não encontrada: id " + divisaoId);
        }
        List<DiaTreino> dias = (diaSemana == null)
                ? repository.findByDivisaoId(divisaoId)
                : repository.findByDivisaoIdAndDiaSemana(divisaoId, diaSemana);

        return dias.stream()
                .sorted(Comparator.comparing(DiaTreino::getDiaSemana))
                .map(DiaTreinoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public DiaTreinoResponse buscar(Long id) {
        return DiaTreinoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public DiaTreinoResponse atualizar(Long id, DiaTreinoRequest request) {
        DiaTreino dia = buscarEntidade(id);
        Long divisaoId = dia.getDivisao().getId();

        if (repository.existsByDivisaoIdAndDiaSemanaAndIdNot(divisaoId, request.diaSemana(), id)) {
            throw new ConflitoException("A divisão '" + dia.getDivisao().getNome()
                    + "' já tem um treino em " + request.diaSemana());
        }

        dia.setNome(request.nome().trim());
        dia.setGrupoMuscular(request.grupoMuscular().trim());
        dia.setDiaSemana(request.diaSemana());
        return DiaTreinoResponse.from(repository.save(dia));
    }

    @Transactional
    public void excluir(Long id) {
        // Os itens do dia (exercícios) entram no 3C, com exclusão em cascata.
        repository.delete(buscarEntidade(id));
    }

    private DiaTreino buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dia de treino não encontrado: id " + id));
    }
}
