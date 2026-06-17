package com.storebase.controller;

import com.storebase.model.Produto;
import com.storebase.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/estoque")
public class EstoqueController {

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public Map<String, Object> listarEstoque() {
        List<Produto> todos = produtoService.listarTodos();

        List<Map<String, Object>> items   = new ArrayList<>();
        List<Map<String, Object>> alertas = new ArrayList<>();

        for (Produto p : todos) {
            String status;
            if (p.getQuantidadeEstoque() < 5)       status = "CRITICO";
            else if (p.getQuantidadeEstoque() < 10) status = "BAIXO";
            else                                     status = "OK";

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("produtoId",   p.getId());
            item.put("nomeProduto", p.getNome());
            item.put("codigo",      p.getCodigo());
            item.put("categoria",   p.getCategoria());
            item.put("quantidade",  p.getQuantidadeEstoque());
            item.put("status",      status);

            items.add(item);
            if (!"OK".equals(status)) alertas.add(item);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("items",   items);
        result.put("alertas", alertas);
        return result;
    }

    @PostMapping("/entrada")
    public Map<String, Object> registrarEntrada(@RequestBody Map<String, Integer> body) {
        int produtoId  = body.get("produtoId");
        int quantidade = body.get("quantidade");
        produtoService.registrarEntrada(produtoId, quantidade);
        Produto p = produtoService.buscarPorId(produtoId);
        return Map.of("produtoId", p.getId(), "novaQuantidade", p.getQuantidadeEstoque());
    }

    @PostMapping("/ajuste")
    public Map<String, Object> ajustarEstoque(@RequestBody Map<String, Integer> body) {
        int produtoId  = body.get("produtoId");
        int quantidade = body.get("quantidade");
        Produto p = produtoService.buscarPorId(produtoId);
        p.setQuantidadeEstoque(quantidade);
        produtoService.atualizar(p);
        return Map.of("produtoId", p.getId(), "novaQuantidade", quantidade);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<java.util.Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("message", e.getMessage()));
    }
}
