package com.neto.treinos_api.service;

import com.neto.treinos_api.dto.DivisaoRequest;
import com.neto.treinos_api.dto.DivisaoResponse;
import com.neto.treinos_api.exception.ConflitoException;
import com.neto.treinos_api.exception.RecursoNaoEncontradoException;
import com.neto.treinos_api.model.Divisao;
import com.neto.treinos_api.repository.DivisaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DivisaoService {

    private final DivisaoRepository repository;

    @Transactional
    public DivisaoResponse criar(DivisaoRequest request) {
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ConflitoException("Já existe uma divisão com o nome '" + nome + "'");
        }
        Divisao divisao = new Divisao();
        divisao.setNome(nome);
        divisao.setAtiva(false);
        return DivisaoResponse.from(repository.save(divisao));
    }

    @Transactional(readOnly = true)
    public List<DivisaoResponse> listar(String nome, Boolean ativa) {
        String filtroNome = nome == null ? "" : nome.trim();
        List<Divisao> resultado = (ativa == null)
                ? repository.findByNomeContainingIgnoreCaseOrderByNome(filtroNome)
                : repository.findByNomeContainingIgnoreCaseAndAtivaOrderByNome(filtroNome, ativa);
        return resultado.stream().map(DivisaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DivisaoResponse buscar(Long id) {
        return DivisaoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public DivisaoResponse atualizar(Long id, DivisaoRequest request) {
        Divisao divisao = buscarEntidade(id);
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ConflitoException("Já existe uma divisão com o nome '" + nome + "'");
        }
        divisao.setNome(nome);
        return DivisaoResponse.from(repository.save(divisao));
    }

    /** Regra de negócio: só uma divisão ativa por vez. Ativar uma desativa a anterior. */
    @Transactional
    public DivisaoResponse ativar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Divisão não encontrada: id " + id);
        }
        repository.desativarTodas();
        Divisao divisao = buscarEntidade(id);
        divisao.setAtiva(true);
        return DivisaoResponse.from(repository.save(divisao));
    }

    @Transactional
    public void excluir(Long id) {
        Divisao divisao = buscarEntidade(id);
        // A exclusão em cascata dos dias entra no 3B; a regra
        // "não excluir divisão com sessões registradas" entra no passo 4.
        repository.delete(divisao);
    }

    private Divisao buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Divisão não encontrada: id " + id));
    }
}