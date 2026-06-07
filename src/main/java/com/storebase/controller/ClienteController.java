package com.storebase.controller;

import com.storebase.model.Cliente;
import com.storebase.model.Venda;
import com.storebase.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping
    public List<Cliente> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(clienteService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public List<Cliente> buscarPorNome(@RequestParam String nome) {
        return clienteService.buscarPorNome(nome);
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<Cliente> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clienteService.buscarPorCpf(cpf));
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody Cliente cliente) {
        clienteService.cadastrar(cliente);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody Cliente cliente) {
        cliente.setId(id);
        clienteService.atualizar(cliente);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        clienteService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/historico")
    public List<Venda> verHistorico(@PathVariable int id) {
        return clienteService.verHistorico(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
