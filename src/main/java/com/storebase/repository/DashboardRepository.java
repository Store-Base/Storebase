package com.storebase.repository;

import com.storebase.config.AppConfig;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class DashboardRepository {

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = AppConfig.getConnection();
        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE DATE(data)=CURRENT_DATE");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("vendasHojeQtd", rs.getInt("qtd"));
                    stats.put("vendasHojeTotal", rs.getDouble("total"));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM cliente");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalClientes", rs.getInt(1));
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM usuario");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalFuncionarios", rs.getInt(1));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT SUM(CASE WHEN quantidade_estoque<5 THEN 1 ELSE 0 END) AS criticos," +
                    "SUM(CASE WHEN quantidade_estoque>=5 AND quantidade_estoque<10 THEN 1 ELSE 0 END) AS baixos FROM produto");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int criticos = rs.getInt("criticos");
                    int baixos = rs.getInt("baixos");
                    stats.put("alertasCriticos", criticos);
                    stats.put("alertasBaixos", baixos);
                    stats.put("alertasEstoque", criticos + baixos);
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM orcamento WHERE status='aberto'");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("orcamentosAbertos", rs.getInt(1));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COALESCE(SUM(valor_total),0) FROM pedido");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalVendasMes", rs.getDouble(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar stats admin: " + e.getMessage());
        }
        return stats;
    }

    public List<Map<String, Object>> getGrafico7Dias() {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT CAST(data AS DATE) AS dia, COALESCE(SUM(valor_total),0) AS total " +
                     "FROM pedido WHERE data >= CURRENT_DATE - INTERVAL '6 days' " +
                     "GROUP BY dia ORDER BY dia";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                String dia = rs.getString("dia");
                row.put("data", dia.length() >= 10 ? dia.substring(5) : dia);
                row.put("total", rs.getDouble("total"));
                lista.add(row);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar grafico: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> getUltimasVendas(int limit) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.valor_total, p.forma_pagamento, p.data, " +
                     "COALESCE(c.nome,'Avulso') AS cliente_nome, u.nome AS func_nome " +
                     "FROM pedido p LEFT JOIN cliente c ON p.cliente_id=c.id " +
                     "JOIN usuario u ON p.usuario_id=u.id ORDER BY p.data DESC LIMIT ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> v = new HashMap<>();
                    v.put("id", rs.getInt("id"));
                    v.put("clienteNome", rs.getString("cliente_nome"));
                    v.put("funcionarioNome", rs.getString("func_nome"));
                    v.put("total", rs.getDouble("valor_total"));
                    v.put("formaPagamento", rs.getString("forma_pagamento"));
                    v.put("dataHora", rs.getString("data"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar ultimas vendas: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Object> getStatsVendedor(int funcId) {
        Map<String, Object> stats = new HashMap<>();
        Connection conn = AppConfig.getConnection();
        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE DATE(data)=CURRENT_DATE AND usuario_id=?")) {
                stmt.setInt(1, funcId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("vendasHojeQtd", rs.getInt("qtd"));
                        stats.put("vendasHojeTotal", rs.getDouble("total"));
                    }
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE usuario_id=?")) {
                stmt.setInt(1, funcId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("totalVendasQtd", rs.getInt("qtd"));
                        stats.put("totalVendasMes", rs.getDouble("total"));
                    }
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM cliente");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("totalClientes", rs.getInt(1));
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM orcamento WHERE status='aberto'");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) stats.put("meusOrcamentosAbertos", rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar stats vendedor: " + e.getMessage());
        }
        return stats;
    }

    public List<Map<String, Object>> getGraficoVendedor(int funcId) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT CAST(data AS DATE) AS dia, COALESCE(SUM(valor_total),0) AS total " +
                     "FROM pedido WHERE usuario_id=? AND data>=CURRENT_DATE-INTERVAL '6 days' " +
                     "GROUP BY dia ORDER BY dia";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, funcId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    String dia = rs.getString("dia");
                    row.put("data", dia.length() >= 10 ? dia.substring(5) : dia);
                    row.put("total", rs.getDouble("total"));
                    lista.add(row);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar grafico vendedor: " + e.getMessage());
        }
        return lista;
    }

    public List<Map<String, Object>> getMinhasVendas(int funcId, int limit) {
        List<Map<String, Object>> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.valor_total, p.forma_pagamento, p.data, " +
                     "COALESCE(c.nome,'Avulso') AS cliente_nome, u.nome AS func_nome " +
                     "FROM pedido p LEFT JOIN cliente c ON p.cliente_id=c.id " +
                     "JOIN usuario u ON p.usuario_id=u.id WHERE p.usuario_id=? " +
                     "ORDER BY p.data DESC LIMIT ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, funcId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> v = new HashMap<>();
                    v.put("id", rs.getInt("id"));
                    v.put("clienteNome", rs.getString("cliente_nome"));
                    v.put("funcionarioNome", rs.getString("func_nome"));
                    v.put("total", rs.getDouble("valor_total"));
                    v.put("formaPagamento", rs.getString("forma_pagamento"));
                    v.put("dataHora", rs.getString("data"));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar minhas vendas: " + e.getMessage());
        }
        return lista;
    }

    public Map<String, Object> getStatsEstoque() {
        Map<String, Object> stats = new HashMap<>();
        List<Map<String, Object>> alertas = new ArrayList<>();
        Connection conn = AppConfig.getConnection();
        try {
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) AS total," +
                    "SUM(CASE WHEN quantidade_estoque<5 THEN 1 ELSE 0 END) AS criticos," +
                    "SUM(CASE WHEN quantidade_estoque>=5 AND quantidade_estoque<10 THEN 1 ELSE 0 END) AS baixos," +
                    "SUM(CASE WHEN quantidade_estoque>=10 THEN 1 ELSE 0 END) AS ok," +
                    "COALESCE(SUM(quantidade_estoque*custo),0) AS valor_total FROM produto");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("totalProdutos", rs.getInt("total"));
                    stats.put("produtosCriticos", rs.getInt("criticos"));
                    stats.put("produtosBaixos", rs.getInt("baixos"));
                    stats.put("produtosOK", rs.getInt("ok"));
                    stats.put("valorTotalEstoque", rs.getDouble("valor_total"));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT id, nome, codigo, quantidade_estoque FROM produto WHERE quantidade_estoque<10 ORDER BY quantidade_estoque");
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> a = new HashMap<>();
                    a.put("produtoId", rs.getInt("id"));
                    a.put("nomeProduto", rs.getString("nome"));
                    a.put("codigo", rs.getString("codigo"));
                    int qty = rs.getInt("quantidade_estoque");
                    a.put("quantidade", qty);
                    a.put("status", qty < 5 ? "CRITICO" : "BAIXO");
                    alertas.add(a);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar stats estoque: " + e.getMessage());
        }
        stats.put("alertas", alertas);
        return stats;
    }
}
