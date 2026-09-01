package com.storebase.controller;

import com.storebase.repository.DashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardRepository dashboardRepository;

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public Map<String, Object> getAdminStats() {
        return dashboardRepository.getAdminStats();
    }

    @GetMapping("/grafico")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<Map<String, Object>> getGrafico() {
        return dashboardRepository.getGrafico7Dias();
    }

    @GetMapping("/ultimas-vendas")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<Map<String, Object>> getUltimasVendas() {
        return dashboardRepository.getUltimasVendas(5);
    }

    @GetMapping("/stats-vendedor")
    @PreAuthorize("hasRole('VENDEDOR')")
    public Map<String, Object> getStatsVendedor(Authentication auth) {
        return dashboardRepository.getStatsVendedor(funcIdDoToken(auth));
    }

    @GetMapping("/grafico-vendedor")
    @PreAuthorize("hasRole('VENDEDOR')")
    public List<Map<String, Object>> getGraficoVendedor(Authentication auth) {
        return dashboardRepository.getGraficoVendedor(funcIdDoToken(auth));
    }

    @GetMapping("/minhas-vendas")
    @PreAuthorize("hasRole('VENDEDOR')")
    public List<Map<String, Object>> getMinhasVendas(Authentication auth) {
        return dashboardRepository.getMinhasVendas(funcIdDoToken(auth), 5);
    }

    @GetMapping("/stats-estoque")
    @PreAuthorize("hasRole('GERENTE_ESTOQUE')")
    public Map<String, Object> getStatsEstoque() {
        return dashboardRepository.getStatsEstoque();
    }

    /**
     * Id do funcionario autenticado, lido do subject do JWT. Nunca de parametro
     * da requisicao: isso impede um vendedor de ver o dashboard de um colega
     * trocando o numero na URL.
     */
    private int funcIdDoToken(Authentication auth) {
        return Integer.parseInt(auth.getName());
    }
}
