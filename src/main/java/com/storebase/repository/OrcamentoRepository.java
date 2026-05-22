package com.storebase.repository;

import com.storebase.model.Orcamento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrcamentoRepository {

    public void salvar(Orcamento orcamento) {
        // TODO: implementar com JDBC
    }

    public void atualizar(Orcamento orcamento) {
        // TODO: implementar com JDBC
    }

    public void deletar(int id) {
        // TODO: implementar com JDBC
    }

    public Optional<Orcamento> buscarPorId(int id) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }

    public List<Orcamento> listarTodos() {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public List<Orcamento> listarPorCliente(int clienteId) {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }
}
