package com.storebase.service;

import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Orcamento;
import com.storebase.model.Venda;
import com.storebase.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private VendaService vendaService;

    public void criar(Orcamento orcamento) {
        if (orcamento.getCliente() == null) {
            throw new IllegalArgumentException("O orçamento deve ter um cliente.");
        }
        orcamentoRepository.salvar(orcamento);
    }

    public void adicionarItem(int orcamentoId, ItemVenda item) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        if ("fechado".equals(orcamento.getStatus())) {
            throw new IllegalStateException("Não é possível alterar um orçamento fechado.");
        }
        orcamento.adicionarItem(item);
        orcamentoRepository.atualizar(orcamento);
    }

    public void removerItem(int orcamentoId, ItemVenda item) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        if ("fechado".equals(orcamento.getStatus())) {
            throw new IllegalStateException("Não é possível alterar um orçamento fechado.");
        }
        orcamento.removerItem(item);
        orcamentoRepository.atualizar(orcamento);
    }

    public Venda converterEmVenda(int orcamentoId, Funcionario funcionario, String formaPagamento) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        if ("fechado".equals(orcamento.getStatus())) {
            throw new IllegalStateException("Este orçamento já foi fechado.");
        }
        if (orcamento.getItens().isEmpty()) {
            throw new IllegalArgumentException("Não é possível converter um orçamento sem itens.");
        }
        Venda venda = new Venda(orcamento.getCliente(), funcionario, formaPagamento);
        for (ItemVenda item : orcamento.getItens()) {
            venda.adicionarItem(item);
        }
        vendaService.registrarVenda(venda);
        orcamento.fechar();
        orcamentoRepository.atualizar(orcamento);
        return venda;
    }

    public Orcamento buscarPorId(int id) {
        return orcamentoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Orçamento não encontrado com id: " + id));
    }

    public List<Orcamento> listarTodos() {
        return orcamentoRepository.listarTodos();
    }

    public List<Orcamento> listarPorCliente(int clienteId) {
        return orcamentoRepository.listarPorCliente(clienteId);
    }
}
