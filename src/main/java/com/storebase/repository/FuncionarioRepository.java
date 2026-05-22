package com.storebase.repository;

import com.storebase.model.Funcionario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioRepository {

    public void salvar(Funcionario funcionario) {
        // TODO: implementar com JDBC
    }

    public void atualizar(Funcionario funcionario) {
        // TODO: implementar com JDBC
    }

    public void deletar(int id) {
        // TODO: implementar com JDBC
    }

    public Optional<Funcionario> buscarPorId(int id) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }

    public List<Funcionario> listarTodos() {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }
}
