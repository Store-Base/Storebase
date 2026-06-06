package com.storebase.service;

import com.storebase.model.Orcamento;
import com.storebase.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    public void criar(Orcamento orcamento) {
        orcamentoRepository.cadastrar(orcamento);
    }

    public Orcamento buscarPorId(int id) {
        return orcamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Orcamento nao encontrado com id: " + id));
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.listarTodos();
    }
}
