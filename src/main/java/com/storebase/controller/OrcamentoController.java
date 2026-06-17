package com.storebase.controller;

import com.storebase.model.ItemOrcamento;
import com.storebase.model.Orcamento;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @GetMapping
    public List<Orcamento> listarTodos() {
        return orcamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orcamento> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> criar(@RequestBody Map<String, Object> body) {
        Orcamento orc = new Orcamento();

        Object clienteId = body.get("clienteId");
        orc.setClienteId(clienteId != null ? ((Number) clienteId).intValue() : 0);

        Object usuarioId = body.get("usuarioId");
        orc.setUsuarioId(usuarioId != null ? ((Number) usuarioId).intValue() : 1);

        String clienteNome = body.get("clienteNome") != null ? (String) body.get("clienteNome") : "Sem nome";
        orc.setNomeComprador(clienteNome);
        orc.setCpfCnpj("N/A");

        Object total = body.get("total");
        orc.setValorTotal(total != null ? ((Number) total).doubleValue() : 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itensJson = (List<Map<String, Object>>) body.get("itens");
        if (itensJson != null) {
            List<ItemOrcamento> itens = new ArrayList<>();
            for (Map<String, Object> itemJson : itensJson) {
                Produto p = new Produto();
                p.setId(((Number) itemJson.get("produtoId")).intValue());
                int qtd = ((Number) itemJson.get("quantidade")).intValue();
                double preco = ((Number) itemJson.get("precoUnitario")).doubleValue();
                itens.add(new ItemOrcamento(0, p, qtd, preco));
            }
            orc.setItens(itens);
        }

        orcamentoService.criar(orc);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id",    orc.getId());
        resp.put("total", orc.getValorTotal());
        return ResponseEntity.status(201).body(resp);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> atualizar(@PathVariable int id, @RequestBody Map<String, Object> body) {
        Orcamento orc = new Orcamento();
        orc.setId(id);

        Object clienteId = body.get("clienteId");
        orc.setClienteId(clienteId != null ? ((Number) clienteId).intValue() : 0);

        Object usuarioId = body.get("usuarioId");
        orc.setUsuarioId(usuarioId != null ? ((Number) usuarioId).intValue() : 1);

        String clienteNome = body.get("clienteNome") != null ? (String) body.get("clienteNome") : "Sem nome";
        orc.setNomeComprador(clienteNome);
        orc.setCpfCnpj("N/A");

        Object total = body.get("total");
        orc.setValorTotal(total != null ? ((Number) total).doubleValue() : 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itensJson = (List<Map<String, Object>>) body.get("itens");
        if (itensJson != null) {
            List<ItemOrcamento> itens = new ArrayList<>();
            for (Map<String, Object> itemJson : itensJson) {
                Produto p = new Produto();
                p.setId(((Number) itemJson.get("produtoId")).intValue());
                int qtd = ((Number) itemJson.get("quantidade")).intValue();
                double preco = ((Number) itemJson.get("precoUnitario")).doubleValue();
                itens.add(new ItemOrcamento(0, p, qtd, preco));
            }
            orc.setItens(itens);
        }

        orcamentoService.atualizar(orc);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id",    orc.getId());
        resp.put("total", orc.getValorTotal());
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        orcamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Void> adicionarItem(@PathVariable int id, @RequestBody ItemOrcamento item) {
        orcamentoService.adicionarItem(id, item);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}/itens/{produtoId}")
    public ResponseEntity<Void> removerItem(@PathVariable int id, @PathVariable int produtoId) {
        orcamentoService.removerItem(id, produtoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/converter")
    public ResponseEntity<Venda> converter(@PathVariable int id) {
        Venda venda = orcamentoService.converterEmVenda(id);
        return ResponseEntity.status(201).body(venda);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
