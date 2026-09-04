package com.storebase.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DashboardRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();

        jdbcTemplate.query(
                "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE DATE(data)=CURRENT_DATE",
                (RowCallbackHandler) rs -> {
                    stats.put("vendasHojeQtd", rs.getInt("qtd"));
                    stats.put("vendasHojeTotal", rs.getDouble("total"));
                });

        stats.put("totalClientes", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cliente", Integer.class));
        stats.put("totalFuncionarios", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM usuario", Integer.class));

        jdbcTemplate.query(
                "SELECT SUM(CASE WHEN quantidade_estoque<5 THEN 1 ELSE 0 END) AS criticos," +
                "SUM(CASE WHEN quantidade_estoque>=5 AND quantidade_estoque<10 THEN 1 ELSE 0 END) AS baixos FROM produto",
                (RowCallbackHandler) rs -> {
                    int criticos = rs.getInt("criticos");
                    int baixos = rs.getInt("baixos");
                    stats.put("alertasCriticos", criticos);
                    stats.put("alertasBaixos", baixos);
                    stats.put("alertasEstoque", criticos + baixos);
                });

        stats.put("orcamentosAbertos",
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orcamento WHERE status='aberto'", Integer.class));
        stats.put("totalVendasMes",
                jdbcTemplate.queryForObject("SELECT COALESCE(SUM(valor_total),0) FROM pedido", Double.class));

        return stats;
    }

    public List<Map<String, Object>> getGrafico7Dias() {
        String sql = "SELECT CAST(data AS DATE) AS dia, COALESCE(SUM(valor_total),0) AS total " +
                     "FROM pedido WHERE data >= CURRENT_DATE - INTERVAL '6 days' " +
                     "GROUP BY dia ORDER BY dia";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            String dia = rs.getString("dia");
            row.put("data", dia.length() >= 10 ? dia.substring(5) : dia);
            row.put("total", rs.getDouble("total"));
            return row;
        });
    }

    public List<Map<String, Object>> getUltimasVendas(int limit) {
        String sql = "SELECT p.id, p.valor_total, p.forma_pagamento, p.data, " +
                     "COALESCE(c.nome,'Avulso') AS cliente_nome, u.nome AS func_nome " +
                     "FROM pedido p LEFT JOIN cliente c ON p.cliente_id=c.id " +
                     "JOIN usuario u ON p.usuario_id=u.id ORDER BY p.data DESC LIMIT ?";
        return jdbcTemplate.query(sql, DashboardRepository::mapVendaResumo, limit);
    }

    public Map<String, Object> getStatsVendedor(int funcId) {
        Map<String, Object> stats = new HashMap<>();

        jdbcTemplate.query(
                "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE DATE(data)=CURRENT_DATE AND usuario_id=?",
                (RowCallbackHandler) rs -> {
                    stats.put("vendasHojeQtd", rs.getInt("qtd"));
                    stats.put("vendasHojeTotal", rs.getDouble("total"));
                }, funcId);

        jdbcTemplate.query(
                "SELECT COUNT(*) AS qtd, COALESCE(SUM(valor_total),0) AS total FROM pedido WHERE usuario_id=?",
                (RowCallbackHandler) rs -> {
                    stats.put("totalVendasQtd", rs.getInt("qtd"));
                    stats.put("totalVendasMes", rs.getDouble("total"));
                }, funcId);

        stats.put("totalClientes", jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cliente", Integer.class));
        stats.put("meusOrcamentosAbertos",
                jdbcTemplate.queryForObject("SELECT COUNT(*) FROM orcamento WHERE status='aberto'", Integer.class));

        return stats;
    }

    public List<Map<String, Object>> getGraficoVendedor(int funcId) {
        String sql = "SELECT CAST(data AS DATE) AS dia, COALESCE(SUM(valor_total),0) AS total " +
                     "FROM pedido WHERE usuario_id=? AND data>=CURRENT_DATE-INTERVAL '6 days' " +
                     "GROUP BY dia ORDER BY dia";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> row = new HashMap<>();
            String dia = rs.getString("dia");
            row.put("data", dia.length() >= 10 ? dia.substring(5) : dia);
            row.put("total", rs.getDouble("total"));
            return row;
        }, funcId);
    }

    public List<Map<String, Object>> getMinhasVendas(int funcId, int limit) {
        String sql = "SELECT p.id, p.valor_total, p.forma_pagamento, p.data, " +
                     "COALESCE(c.nome,'Avulso') AS cliente_nome, u.nome AS func_nome " +
                     "FROM pedido p LEFT JOIN cliente c ON p.cliente_id=c.id " +
                     "JOIN usuario u ON p.usuario_id=u.id WHERE p.usuario_id=? " +
                     "ORDER BY p.data DESC LIMIT ?";
        return jdbcTemplate.query(sql, DashboardRepository::mapVendaResumo, funcId, limit);
    }

    public Map<String, Object> getStatsEstoque() {
        Map<String, Object> stats = new HashMap<>();

        jdbcTemplate.query(
                "SELECT COUNT(*) AS total," +
                "SUM(CASE WHEN quantidade_estoque<5 THEN 1 ELSE 0 END) AS criticos," +
                "SUM(CASE WHEN quantidade_estoque>=5 AND quantidade_estoque<10 THEN 1 ELSE 0 END) AS baixos," +
                "SUM(CASE WHEN quantidade_estoque>=10 THEN 1 ELSE 0 END) AS ok," +
                "COALESCE(SUM(quantidade_estoque*custo),0) AS valor_total FROM produto",
                (RowCallbackHandler) rs -> {
                    stats.put("totalProdutos", rs.getInt("total"));
                    stats.put("produtosCriticos", rs.getInt("criticos"));
                    stats.put("produtosBaixos", rs.getInt("baixos"));
                    stats.put("produtosOK", rs.getInt("ok"));
                    stats.put("valorTotalEstoque", rs.getDouble("valor_total"));
                });

        List<Map<String, Object>> alertas = jdbcTemplate.query(
                "SELECT id, nome, codigo, quantidade_estoque FROM produto WHERE quantidade_estoque<10 ORDER BY quantidade_estoque",
                (rs, rowNum) -> {
                    Map<String, Object> a = new HashMap<>();
                    a.put("produtoId", rs.getInt("id"));
                    a.put("nomeProduto", rs.getString("nome"));
                    a.put("codigo", rs.getString("codigo"));
                    int qty = rs.getInt("quantidade_estoque");
                    a.put("quantidade", qty);
                    a.put("status", qty < 5 ? "CRITICO" : "BAIXO");
                    return a;
                });

        stats.put("alertas", alertas);
        return stats;
    }

    private static Map<String, Object> mapVendaResumo(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> v = new HashMap<>();
        v.put("id", rs.getInt("id"));
        v.put("clienteNome", rs.getString("cliente_nome"));
        v.put("funcionarioNome", rs.getString("func_nome"));
        v.put("total", rs.getDouble("valor_total"));
        v.put("formaPagamento", rs.getString("forma_pagamento"));
        v.put("dataHora", rs.getString("data"));
        return v;
    }
}
