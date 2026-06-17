package com.storebase.controller;

import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/vendas")
    public List<Venda> listarVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return relatorioService.listarVendasPorPeriodo(dataInicio, dataFim);
    }

    @GetMapping("/estoque")
    public List<Produto> listarEstoque() {
        return relatorioService.listarEstoque();
    }

    @GetMapping("/faturamento")
    public Map<String, Double> calcularFaturamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return relatorioService.calcularFaturamento(dataInicio, dataFim);
    }

    @GetMapping("/produtos-mais-vendidos")
    public List<Map<String, Object>> listarMaisVendidos(
            @RequestParam(defaultValue = "10") int limite) {
        return relatorioService.listarMaisVendidos(limite);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
