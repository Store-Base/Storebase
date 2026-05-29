package com.storebase.controller;

import com.storebase.model.Venda;
import com.storebase.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public List<Venda> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Venda> listarPorCliente(@PathVariable int clienteId) {
        return vendaService.listarPorCliente(clienteId);
    }

    @PostMapping
    public ResponseEntity<Void> registrarVenda(@RequestBody Venda venda) {
        vendaService.registrarVenda(venda);
        return ResponseEntity.status(201).build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
