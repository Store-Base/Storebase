package com.storebase.controller;

import com.storebase.model.ItemOrcamento;
import com.storebase.model.Orcamento;
import com.storebase.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Void> criar(@RequestBody Orcamento orcamento) {
        orcamentoService.criar(orcamento);
        return ResponseEntity.status(201).build();
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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
