package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.ItemDiaTreinoRequest;
import com.neto.treinos_api.dto.ItemDiaTreinoResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.model.DiaTreino;
import com.neto.treinos_api.model.Exercicio;
import com.neto.treinos_api.model.ItemDiaTreino;
import com.neto.treinos_api.repository.DiaTreinoRepository;
import com.neto.treinos_api.repository.ExercicioRepository;
import com.neto.treinos_api.repository.ItemDiaTreinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemDiaTreinoService {

    private final ItemDiaTreinoRepository repository;
    private final DiaTreinoRepository diaTreinoRepository;
    private final ExercicioRepository exercicioRepository;

    /** Regra de negócio: o mesmo exercício não pode aparecer duas vezes no mesmo dia. */
    @Transactional
    public ItemDiaTreinoResponse criar(Long diaTreinoId, ItemDiaTreinoRequest request) {
        DiaTreino dia = diaTreinoRepository.findById(diaTreinoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Dia de treino não encontrado: id " + diaTreinoId));
        Exercicio exercicio = buscarExercicio(request.exercicioId());

        if (repository.existsByDiaTreinoIdAndExercicioId(diaTreinoId, exercicio.getId())) {
            throw new ConflitoException("O exercício '" + exercicio.getNome()
                    + "' já está no dia '" + dia.getNome() + "'");
        }

        ItemDiaTreino item = new ItemDiaTreino();
        item.setDiaTreino(dia);
        item.setExercicio(exercicio);
        item.setSeries(request.series());
        item.setRepeticoes(request.repeticoes());
        item.setDescansoSegundos(request.descansoSegundos());
        item.setOrdem(request.ordem() != null
                ? request.ordem()
                : repository.maiorOrdem(diaTreinoId) + 1);
        return ItemDiaTreinoResponse.from(repository.save(item));
    }

    @Transactional(readOnly = true)
    public List<ItemDiaTreinoResponse> listar(Long diaTreinoId) {
        if (!diaTreinoRepository.existsById(diaTreinoId)) {
            throw new RecursoNaoEncontradoException("Dia de treino não encontrado: id " + diaTreinoId);
        }
        return repository.findByDiaTreinoIdOrderByOrdemAscIdAsc(diaTreinoId).stream()
                .map(ItemDiaTreinoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemDiaTreinoResponse buscar(Long id) {
        return ItemDiaTreinoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ItemDiaTreinoResponse atualizar(Long id, ItemDiaTreinoRequest request) {
        ItemDiaTreino item = buscarEntidade(id);
        Exercicio exercicio = buscarExercicio(request.exercicioId());
        Long diaTreinoId = item.getDiaTreino().getId();

        if (repository.existsByDiaTreinoIdAndExercicioIdAndIdNot(diaTreinoId, exercicio.getId(), id)) {
            throw new ConflitoException("O exercício '" + exercicio.getNome()
                    + "' já está no dia '" + item.getDiaTreino().getNome() + "'");
        }

        item.setExercicio(exercicio);
        item.setSeries(request.series());
        item.setRepeticoes(request.repeticoes());
        item.setDescansoSegundos(request.descansoSegundos());
        if (request.ordem() != null) {
            item.setOrdem(request.ordem());
        }
        return ItemDiaTreinoResponse.from(repository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private ItemDiaTreino buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item de treino não encontrado: id " + id));
    }

    private Exercicio buscarExercicio(Long exercicioId) {
        return exercicioRepository.findById(exercicioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercício não encontrado: id " + exercicioId));
    }
}
