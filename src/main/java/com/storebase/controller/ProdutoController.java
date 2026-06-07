package com.storebase.controller;

import com.storebase.model.EntradaEstoque;
import com.storebase.model.Produto;
import com.storebase.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> listarTodos() {
        return produtoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public List<Produto> buscarPorNome(@RequestParam String nome) {
        return produtoService.buscarPorNome(nome);
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Produto> buscarPorCodigo(@PathVariable String codigo) {
        return produtoService.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody Produto produto) {
        produtoService.cadastrar(produto);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody Produto produto) {
        produto.setId(id);
        produtoService.atualizar(produto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<Void> registrarEntrada(@PathVariable int id, @RequestBody EntradaEstoque entrada) {
        produtoService.registrarEntrada(id, entrada.getQuantidade());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estoque-baixo")
    public List<Produto> listarEstoqueBaixo(@RequestParam(defaultValue = "10") int limite) {
        return produtoService.listarEstoqueBaixo(limite);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
