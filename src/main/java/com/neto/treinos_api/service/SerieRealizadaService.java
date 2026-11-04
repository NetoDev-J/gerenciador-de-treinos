package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.SerieRealizadaRequest;
import com.neto.treinos_api.dto.SerieRealizadaResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.model.Exercicio;
import com.neto.treinos_api.model.SerieRealizada;
import com.neto.treinos_api.model.SessaoTreino;
import com.neto.treinos_api.repository.ExercicioRepository;
import com.neto.treinos_api.repository.ItemDiaTreinoRepository;
import com.neto.treinos_api.repository.SerieRealizadaRepository;
import com.neto.treinos_api.repository.SessaoTreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SerieRealizadaService {

    private final SerieRealizadaRepository repository;
    private final SessaoTreinoRepository sessaoRepository;
    private final ExercicioRepository exercicioRepository;
    private final ItemDiaTreinoRepository itemRepository;

    @Transactional
    public SerieRealizadaResponse criar(Long sessaoId, SerieRealizadaRequest request) {
        SessaoTreino sessao = sessaoRepository.findById(sessaoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Sessão não encontrada: id " + sessaoId));
        Exercicio exercicio = buscarExercicio(request.exercicioId());

        validarExercicioNoDia(sessao, exercicio);

        int numero = request.numeroSerie() != null
                ? request.numeroSerie()
                : repository.maiorNumeroSerie(sessaoId, exercicio.getId()) + 1;

        if (repository.existsBySessaoIdAndExercicioIdAndNumeroSerie(sessaoId, exercicio.getId(), numero)) {
            throw new ConflitoException("A série " + numero + " de '" + exercicio.getNome()
                    + "' já foi registrada nesta sessão");
        }

        SerieRealizada serie = new SerieRealizada();
        serie.setSessao(sessao);
        serie.setExercicio(exercicio);
        serie.setNumeroSerie(numero);
        serie.setRepeticoes(request.repeticoes());
        serie.setCarga(request.carga());
        return SerieRealizadaResponse.from(repository.save(serie));
    }

    @Transactional(readOnly = true)
    public List<SerieRealizadaResponse> listar(Long sessaoId, Long exercicioId) {
        if (!sessaoRepository.existsById(sessaoId)) {
            throw new RecursoNaoEncontradoException("Sessão não encontrada: id " + sessaoId);
        }
        List<SerieRealizada> series = (exercicioId == null)
                ? repository.findBySessaoIdOrderByIdAsc(sessaoId)
                : repository.findBySessaoIdAndExercicioIdOrderByNumeroSerieAsc(sessaoId, exercicioId);
        return series.stream().map(SerieRealizadaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public SerieRealizadaResponse buscar(Long id) {
        return SerieRealizadaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public SerieRealizadaResponse atualizar(Long id, SerieRealizadaRequest request) {
        SerieRealizada serie = buscarEntidade(id);
        Long sessaoId = serie.getSessao().getId();
        Exercicio exercicio = buscarExercicio(request.exercicioId());

        // Só revalida o pertencimento se o exercício mudou: assim dá para corrigir
        // a carga de uma série antiga mesmo que o exercício tenha saído do plano do dia.
        if (!exercicio.getId().equals(serie.getExercicio().getId())) {
            validarExercicioNoDia(serie.getSessao(), exercicio);
        }

        int numero = request.numeroSerie() != null ? request.numeroSerie() : serie.getNumeroSerie();

        if (repository.existsBySessaoIdAndExercicioIdAndNumeroSerieAndIdNot(
                sessaoId, exercicio.getId(), numero, id)) {
            throw new ConflitoException("A série " + numero + " de '" + exercicio.getNome()
                    + "' já foi registrada nesta sessão");
        }

        serie.setExercicio(exercicio);
        serie.setNumeroSerie(numero);
        serie.setRepeticoes(request.repeticoes());
        serie.setCarga(request.carga());
        return SerieRealizadaResponse.from(repository.save(serie));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /** Regra: a série só pode ser de um exercício que pertence ao dia de treino da sessão. */
    private void validarExercicioNoDia(SessaoTreino sessao, Exercicio exercicio) {
        Long diaId = sessao.getDiaTreino().getId();
        if (!itemRepository.existsByDiaTreinoIdAndExercicioId(diaId, exercicio.getId())) {
            throw new ConflitoException("O exercício '" + exercicio.getNome()
                    + "' não faz parte do treino '" + sessao.getDiaTreino().getNome() + "' desta sessão");
        }
    }

    private SerieRealizada buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Série não encontrada: id " + id));
    }

    private Exercicio buscarExercicio(Long exercicioId) {
        return exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercício não encontrado: id " + exercicioId));
    }
}
