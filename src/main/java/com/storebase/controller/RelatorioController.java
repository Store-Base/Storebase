package com.storebase.controller;

import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @GetMapping("/vendas")
    public Map<String, Object> listarVendasPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        List<Venda> vendas = relatorioService.listarVendasPorPeriodo(dataInicio, dataFim);

        double totalGeral = 0;
        Map<String, Double> porPagamento = new LinkedHashMap<>();
        for (Venda v : vendas) {
            totalGeral += v.getValorTotal();
            String fp = v.getFormaPagamento() != null ? v.getFormaPagamento() : "OUTROS";
            porPagamento.merge(fp, v.getValorTotal(), Double::sum);
        }

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("quantidadeTotal", vendas.size());
        resp.put("totalGeral", totalGeral);
        resp.put("porPagamento", porPagamento);
        return resp;
    }

    @GetMapping("/estoque")
    public List<Map<String, Object>> listarEstoque() {
        List<Produto> produtos = relatorioService.listarEstoque();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Produto p : produtos) {
            String status = p.getQuantidadeEstoque() < 5 ? "CRITICO"
                    : p.getQuantidadeEstoque() < 10 ? "BAIXO" : "OK";
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nomeProduto", p.getNome());
            m.put("codigo", p.getCodigo());
            m.put("quantidade", p.getQuantidadeEstoque());
            m.put("status", status);
            result.add(m);
        }
        return result;
    }

    @GetMapping("/faturamento")
    public List<Map<String, Object>> calcularFaturamentoPorAno(
            @RequestParam int ano) {
        return relatorioService.listarFaturamentoPorAno(ano);
    }

    @GetMapping("/produtos")
    public List<Map<String, Object>> listarProdutosPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return relatorioService.listarProdutosPorPeriodo(dataInicio, dataFim);
    }

    @GetMapping("/clientes")
    public List<Map<String, Object>> listarClientesComHistorico() {
        return relatorioService.listarClientesComHistorico();
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
