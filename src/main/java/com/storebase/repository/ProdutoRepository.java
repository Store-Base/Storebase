package com.storebase.repository;

import com.storebase.model.Produto;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProdutoRepository {

    public void salvar(Produto produto) {
        // TODO: implementar com JDBC
    }

    public void atualizar(Produto produto) {
        // TODO: implementar com JDBC
    }

    public void deletar(int id) {
        // TODO: implementar com JDBC
    }

    public Optional<Produto> buscarPorId(int id) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }

    public List<Produto> listarTodos() {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public List<Produto> buscarPorNome(String nome) {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }
}
