package com.storebase.service;

import com.storebase.model.Cliente;
import com.storebase.model.Funcionario;
import com.storebase.model.ItemOrcamento;
import com.storebase.model.ItemVenda;
import com.storebase.model.Orcamento;
import com.storebase.model.Produto;
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
    private ProdutoService produtoService;

    @Autowired
    private VendaService vendaService;

    public void criar(Orcamento orcamento) {
        orcamentoRepository.cadastrar(orcamento);
    }

    public void atualizar(Orcamento orcamento) {
        buscarPorId(orcamento.getId());
        orcamentoRepository.atualizar(orcamento);
    }

    public void deletar(int id) {
        buscarPorId(id);
        orcamentoRepository.deletar(id);
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

    public Venda converterEmVenda(int orcamentoId) {
        Orcamento orcamento = buscarPorId(orcamentoId);

        if ("convertido".equals(orcamento.getStatus())) {
            throw new IllegalArgumentException("Orcamento " + orcamentoId + " ja foi convertido em venda.");
        }
        if (orcamento.getClienteId() <= 0) {
            throw new IllegalArgumentException("Orcamento sem cliente vinculado nao pode ser convertido em venda.");
        }
        if (orcamento.getItens().isEmpty()) {
            throw new IllegalArgumentException("Orcamento sem itens nao pode ser convertido em venda.");
        }

        Cliente cliente = new Cliente();
        cliente.setId(orcamento.getClienteId());

        Funcionario funcionario = new Funcionario();
        funcionario.setId(orcamento.getUsuarioId());

        Venda venda = new Venda(cliente, funcionario);

        for (ItemOrcamento itemOrc : orcamento.getItens()) {
            // usa o preco do orcamento para honrar o valor acordado
            Produto produtoStub = new Produto();
            produtoStub.setId(itemOrc.getProduto().getId());
            produtoStub.setPrecoVenda(itemOrc.getPrecoUnitario());
            venda.getItens().add(new ItemVenda(produtoStub, itemOrc.getQuantidade()));
        }

        vendaService.registrarVenda(venda);
        orcamentoRepository.atualizarStatus(orcamentoId, "convertido");

        return venda;
    }
}
