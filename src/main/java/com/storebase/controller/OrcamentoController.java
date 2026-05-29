package com.storebase.controller;

import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Orcamento;
import com.storebase.model.Venda;
import com.storebase.service.FuncionarioService;
import com.storebase.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orcamentos")
public class OrcamentoController {

    @Autowired
    private OrcamentoService orcamentoService;

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public List<Orcamento> listarTodos() {
        return orcamentoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Orcamento> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(orcamentoService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Orcamento> listarPorCliente(@PathVariable int clienteId) {
        return orcamentoService.listarPorCliente(clienteId);
    }

    @PostMapping
    public ResponseEntity<Void> criar(@RequestBody Orcamento orcamento) {
        orcamentoService.criar(orcamento);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/{id}/itens")
    public ResponseEntity<Void> adicionarItem(@PathVariable int id, @RequestBody ItemVenda item) {
        orcamentoService.adicionarItem(id, item);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/itens")
    public ResponseEntity<Void> removerItem(@PathVariable int id, @RequestBody ItemVenda item) {
        orcamentoService.removerItem(id, item);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/converter")
    public ResponseEntity<Venda> converterEmVenda(
            @PathVariable int id,
            @RequestBody Map<String, Object> body) {
        int funcionarioId = ((Number) body.get("funcionarioId")).intValue();
        String formaPagamento = (String) body.get("formaPagamento");
        Funcionario funcionario = funcionarioService.buscarPorId(funcionarioId);
        Venda venda = orcamentoService.converterEmVenda(id, funcionario, formaPagamento);
        return ResponseEntity.status(201).body(venda);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
