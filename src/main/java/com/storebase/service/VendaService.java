package com.storebase.service;

import com.storebase.model.Comprovante;
import com.storebase.model.ItemVenda;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.repository.ProdutoRepository;
import com.storebase.repository.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    public void registrarVenda(Venda venda) {
        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("A venda deve conter ao menos um item.");
        }

        // Busca os produtos reais no banco antes de validar
        for (ItemVenda item : venda.getItens()) {
            Produto produtoDoBanco = produtoRepository.buscarPorId(item.getProduto().getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Produto não encontrado com id: " + item.getProduto().getId()));
            item.setProduto(produtoDoBanco); // substitui o objeto parcial pelo completo

            if (produtoDoBanco.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para o produto: " + produtoDoBanco.getNome()
                                + ". Disponivel: " + produtoDoBanco.getQuantidadeEstoque()
                                + ", solicitado: " + item.getQuantidade());
            }
        }

        venda.calcularTotal();
        vendaRepository.salvar(venda);

        for (ItemVenda item : venda.getItens()) {
            Produto produto = item.getProduto();
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.atualizar(produto);
        }
    }

    public Venda buscarPorId(int id) {
        return vendaRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda nao encontrada com id: " + id));
    }

    public List<Venda> listarTodas() {
        return vendaRepository.listarTodas();
    }

    public List<Venda> listarPorCliente(int clienteId) {
        return vendaRepository.listarPorCliente(clienteId);
    }

    public Comprovante gerarComprovante(int vendaId) {
        Venda venda = buscarPorId(vendaId);
        Comprovante comprovante = new Comprovante();
        comprovante.setNumeroVenda(venda.getId());
        comprovante.setData(venda.getData().toString());
        comprovante.setNomeCliente(venda.getCliente().getNome());
        comprovante.setNomeFuncionario(venda.getFuncionario().getNome());
        comprovante.setItens(venda.getItens());
        comprovante.setDesconto(venda.getDesconto());
        comprovante.setValorTotal(venda.getValorTotal());
        comprovante.setFormaPagamento(venda.getFormaPagamento());
        return comprovante;
    }
}
