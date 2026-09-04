package com.storebase.repository;

import com.storebase.model.Cliente;
import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class RelatorioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Venda> VENDA_MAPPER = (rs, rowNum) -> {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setValorTotal(rs.getDouble("valor_total"));
        v.setDesconto(rs.getDouble("desconto"));
        v.setFormaPagamento(rs.getString("forma_pagamento"));
        v.setStatus(rs.getString("status"));
        v.setData(rs.getDate("data").toLocalDate());
        Cliente c = new Cliente();
        c.setId(rs.getInt("c_id"));
        c.setNome(rs.getString("c_nome"));
        c.setCpf(rs.getString("c_cpf"));
        c.setEmail(rs.getString("c_email"));
        c.setEndereco(rs.getString("c_endereco"));
        v.setCliente(c);
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("u_id"));
        f.setNome(rs.getString("u_nome"));
        f.setCargo(rs.getString("u_cargo"));
        f.setLogin(rs.getString("u_login"));
        v.setFuncionario(f);
        return v;
    };

    private static final RowMapper<Produto> PRODUTO_MAPPER = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setCategoria(rs.getString("categoria"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return p;
    };

    private static final RowMapper<ItemVenda> ITEM_MAPPER = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("p_id"));
        p.setNome(rs.getString("p_nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return new ItemVenda(p, rs.getInt("quantidade"));
    };

    public List<Venda> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.status, v.data, " +
                     "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
                     "u.id AS u_id, u.nome AS u_nome, u.cargo AS u_cargo, u.login AS u_login " +
                     "FROM pedido v " +
                     "JOIN cliente c ON v.cliente_id = c.id " +
                     "JOIN usuario u ON v.usuario_id = u.id " +
                     "WHERE DATE(v.data) BETWEEN ? AND ? " +
                     "ORDER BY v.data DESC";
        List<Venda> lista = jdbcTemplate.query(sql, VENDA_MAPPER,
                Date.valueOf(dataInicio), Date.valueOf(dataFim));
        for (Venda v : lista) {
            v.setItens(carregarItens(v.getId()));
        }
        return lista;
    }

    public List<Produto> listarEstoque() {
        String sql = "SELECT * FROM produto ORDER BY quantidade_estoque ASC, nome ASC";
        return jdbcTemplate.query(sql, PRODUTO_MAPPER);
    }

    public Map<String, Double> calcularFaturamento(LocalDate dataInicio, LocalDate dataFim) {
        Map<String, Double> resultado = new HashMap<>();
        String sql = "SELECT COALESCE(SUM(valor_total), 0) AS bruto, " +
                     "COALESCE(SUM(valor_total - desconto), 0) AS liquido " +
                     "FROM pedido " +
                     "WHERE DATE(data) BETWEEN ? AND ?";
        jdbcTemplate.query(sql, (RowCallbackHandler) rs -> {
            resultado.put("faturamentoBruto", rs.getDouble("bruto"));
            resultado.put("faturamentoLiquido", rs.getDouble("liquido"));
        }, Date.valueOf(dataInicio), Date.valueOf(dataFim));
        return resultado;
    }

    public List<Map<String, Object>> listarMaisVendidos(int limite) {
        String sql = "SELECT p.id, p.nome, p.codigo, p.categoria, p.preco_venda, " +
                     "SUM(ip.quantidade) AS total_vendido, " +
                     "SUM(ip.quantidade * ip.preco_unitario) AS total_faturado " +
                     "FROM item_pedido ip " +
                     "JOIN produto p ON ip.produto_id = p.id " +
                     "GROUP BY p.id, p.nome, p.codigo, p.categoria, p.preco_venda " +
                     "ORDER BY total_vendido DESC " +
                     "LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("produtoId", rs.getInt("id"));
            item.put("nome", rs.getString("nome"));
            item.put("codigo", rs.getString("codigo"));
            item.put("categoria", rs.getString("categoria"));
            item.put("precoVenda", rs.getDouble("preco_venda"));
            item.put("totalVendido", rs.getInt("total_vendido"));
            item.put("totalFaturado", rs.getDouble("total_faturado"));
            return item;
        }, limite);
    }

    public List<Map<String, Object>> listarFaturamentoPorAno(int ano) {
        String sql = "SELECT TO_CHAR(DATE_TRUNC('month', data), 'Mon') AS mes, " +
                     "EXTRACT(MONTH FROM data) AS mes_num, " +
                     "COALESCE(SUM(valor_total), 0) AS bruto, " +
                     "COALESCE(SUM(valor_total - desconto), 0) AS liquido " +
                     "FROM pedido " +
                     "WHERE EXTRACT(YEAR FROM data) = ? " +
                     "GROUP BY DATE_TRUNC('month', data), EXTRACT(MONTH FROM data) " +
                     "ORDER BY mes_num";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("mes", rs.getString("mes"));
            item.put("receitaBruta", rs.getDouble("bruto"));
            item.put("receitaLiquida", rs.getDouble("liquido"));
            return item;
        }, ano);
    }

    public List<Map<String, Object>> listarClientesComHistorico() {
        String sql = "SELECT c.nome, c.cpf, " +
                     "COUNT(v.id) AS qtd_compras, " +
                     "COALESCE(SUM(v.valor_total - v.desconto), 0) AS total_compras " +
                     "FROM cliente c " +
                     "LEFT JOIN pedido v ON c.id = v.cliente_id " +
                     "GROUP BY c.id, c.nome, c.cpf " +
                     "ORDER BY total_compras DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("nome", rs.getString("nome"));
            item.put("cpf", rs.getString("cpf"));
            item.put("qtdCompras", rs.getInt("qtd_compras"));
            item.put("totalCompras", rs.getDouble("total_compras"));
            return item;
        });
    }

    public List<Map<String, Object>> listarProdutosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        String sql = "SELECT p.nome AS produto, SUM(ip.quantidade) AS quantidade_vendida " +
                     "FROM item_pedido ip " +
                     "JOIN produto p ON ip.produto_id = p.id " +
                     "JOIN pedido v ON ip.pedido_id = v.id " +
                     "WHERE DATE(v.data) BETWEEN ? AND ? " +
                     "GROUP BY p.id, p.nome " +
                     "ORDER BY quantidade_vendida DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("produto", rs.getString("produto"));
            item.put("quantidadeVendida", rs.getInt("quantidade_vendida"));
            return item;
        }, Date.valueOf(dataInicio), Date.valueOf(dataFim));
    }

    private List<ItemVenda> carregarItens(int vendaId) {
        String sql = "SELECT i.quantidade, i.preco_unitario, " +
                     "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, p.quantidade_estoque " +
                     "FROM item_pedido i JOIN produto p ON i.produto_id = p.id " +
                     "WHERE i.pedido_id = ?";
        return jdbcTemplate.query(sql, ITEM_MAPPER, vendaId);
    }
}
