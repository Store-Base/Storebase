package com.storebase.service;

import com.storebase.model.ItemOrcamento;
import com.storebase.model.Orcamento;
import com.storebase.model.Produto;
import com.storebase.repository.OrcamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrcamentoService {

    @Autowired
    private OrcamentoRepository orcamentoRepository;

    @Autowired
    private ProdutoService produtoService;

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

    public void adicionarItem(int orcamentoId, ItemOrcamento item) {
        Orcamento orcamento = buscarPorId(orcamentoId);
        Produto produto = produtoService.buscarPorId(item.getProduto().getId());

        item.setOrcamentoId(orcamentoId);
        item.setProduto(produto);
        item.setPrecoUnitario(produto.getPrecoVenda());
        item.calcularSubtotal();

        orcamentoRepository.adicionarItem(orcamentoId, item);

        orcamento.getItens().add(item);
        orcamento.calcularTotal();
        orcamentoRepository.atualizarValorTotal(orcamentoId, orcamento.getValorTotal());
    }

    public void removerItem(int orcamentoId, int produtoId) {
        buscarPorId(orcamentoId);
        orcamentoRepository.removerItem(orcamentoId, produtoId);

        Orcamento orcamento = buscarPorId(orcamentoId);
        orcamento.calcularTotal();
        orcamentoRepository.atualizarValorTotal(orcamentoId, orcamento.getValorTotal());
    }
}
