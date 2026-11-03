package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.ExercicioRequest;
import com.neto.treinos_api.dto.ExercicioResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.model.Exercicio;
import com.neto.treinos_api.repository.ExercicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExercicioService {

    private final ExercicioRepository repository;

    @Transactional
    public ExercicioResponse criar(ExercicioRequest request) {
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoException("Já existe um exercício com o nome '" + nome + "'");
        }
        Exercicio exercicio = new Exercicio();
        exercicio.setNome(nome);
        exercicio.setGrupoMuscular(request.grupoMuscular().trim());
        return ExercicioResponse.from(repository.save(exercicio));
    }

    @Transactional(readOnly = true)
    public List<ExercicioResponse> listar(String nome, String grupoMuscular) {
        String filtroNome = nome == null ? "" : nome.trim();
        String filtroGrupo = grupoMuscular == null ? "" : grupoMuscular.trim();
        return repository.filtrar(filtroNome, filtroGrupo).stream()
                .map(ExercicioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ExercicioResponse buscar(Long id) {
        return ExercicioResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ExercicioResponse atualizar(Long id, ExercicioRequest request) {
        Exercicio exercicio = buscarEntidade(id);
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe um exercício com o nome '" + nome + "'");
        }
        exercicio.setNome(nome);
        exercicio.setGrupoMuscular(request.grupoMuscular().trim());
        return ExercicioResponse.from(repository.save(exercicio));
    }

    @Transactional
    public void excluir(Long id) {
        Exercicio exercicio = buscarEntidade(id);
        // A regra "não excluir exercício em uso" entra nos passos 3 e 4,
        // quando existirem as tabelas que referenciam o exercício.
        repository.delete(exercicio);
    }

    private Exercicio buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exercício não encontrado: id " + id));
    }
}