package com.storebase.repository;

import com.storebase.model.Venda;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VendaRepository {

    public void salvar(Venda venda) {
        // TODO: implementar com JDBC (salvar venda + itens_venda)
    }

    public Optional<Venda> buscarPorId(int id) {
        // TODO: implementar com JDBC
        return Optional.empty();
    }

    public List<Venda> listarTodas() {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }

    public List<Venda> listarPorCliente(int clienteId) {
        // TODO: implementar com JDBC
        return new ArrayList<>();
    }
}
