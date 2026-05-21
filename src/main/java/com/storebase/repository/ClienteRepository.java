package com.storebase.repository;

import com.storebase.model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ClienteRepository {

    public void salvar(Cliente cliente) {
        // TODO: implementar com JDBC
    }

    public void atualizar(Cliente cliente) {
        // TODO: implementar com JDBC
    }

    public void deletar(int id) {
        // TODO: implementar com JDBC
    }

    public Optional<Cliente> buscarPorId(int id) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }

    public List<Cliente> listarTodos() {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public List<Cliente> buscarPorNome(String nome) {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }
}
