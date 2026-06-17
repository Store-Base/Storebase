package com.storebase.controller;

import com.storebase.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardRepository dashboardRepository;

    @GetMapping("/stats")
    public Map<String, Object> getAdminStats() {
        return dashboardRepository.getAdminStats();
    }

    @GetMapping("/grafico")
    public List<Map<String, Object>> getGrafico() {
        return dashboardRepository.getGrafico7Dias();
    }

    @GetMapping("/ultimas-vendas")
    public List<Map<String, Object>> getUltimasVendas() {
        return dashboardRepository.getUltimasVendas(5);
    }

    @GetMapping("/stats-vendedor")
    public Map<String, Object> getStatsVendedor(@RequestParam int funcId) {
        return dashboardRepository.getStatsVendedor(funcId);
    }

    @GetMapping("/grafico-vendedor")
    public List<Map<String, Object>> getGraficoVendedor(@RequestParam int funcId) {
        return dashboardRepository.getGraficoVendedor(funcId);
    }

    @GetMapping("/minhas-vendas")
    public List<Map<String, Object>> getMinhasVendas(@RequestParam int funcId) {
        return dashboardRepository.getMinhasVendas(funcId, 5);
    }

    @GetMapping("/stats-estoque")
    public Map<String, Object> getStatsEstoque() {
        return dashboardRepository.getStatsEstoque();
    }
}
