package com.storebase.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.storebase.model.Produto;
import com.storebase.repository.ProdutoRepository;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public void cadastrar(Produto produto) {
        if (produto.getPrecoVenda() <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que zero.");
        }
        if (produto.getCodigo() == null || produto.getCodigo().isBlank()) {
            throw new IllegalArgumentException("Código do produto não pode ser vazio.");
        }
        produtoRepository.buscarPorCodigo(produto.getCodigo()).ifPresent(p -> {
            throw new IllegalArgumentException("Já existe um produto com o código: " + produto.getCodigo());
        });
        produtoRepository.salvar(produto);
    }

    public void atualizar(Produto produto) {
        if (produto.getPrecoVenda() <= 0) {
            throw new IllegalArgumentException("Preço de venda deve ser maior que zero.");
        }
        buscarPorId(produto.getId());
        produtoRepository.atualizar(produto);
    }

    public void deletar(int id) {
        buscarPorId(id);
        produtoRepository.deletar(id);
    }

    public Produto buscarPorId(int id) {
        return produtoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com id: " + id));
    }

    public List<Produto> listarTodos() {
        return produtoRepository.listarTodos();
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.buscarPorNome(nome);
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        return produtoRepository.buscarPorCodigo(codigo);
    }
}
