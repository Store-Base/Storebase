package com.storebase.repository;

import com.storebase.model.HistoricoPreco;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HistoricoPrecoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<HistoricoPreco> MAPPER = (rs, rowNum) -> {
        HistoricoPreco h = new HistoricoPreco();
        h.setId(rs.getInt("id"));
        h.setProdutoId(rs.getInt("produto_id"));
        h.setPrecoAnterior(rs.getDouble("preco_anterior"));
        h.setPrecoNovo(rs.getDouble("preco_novo"));
        h.setCustoAnterior(rs.getDouble("custo_anterior"));
        h.setCustoNovo(rs.getDouble("custo_novo"));
        h.setDataAlteracao(rs.getTimestamp("data_alteracao").toLocalDateTime());
        return h;
    };

    public void registrar(int produtoId, double precoAnterior, double precoNovo,
                           double custoAnterior, double custoNovo) {
        String sql = "INSERT INTO historico_preco_produto (produto_id, preco_anterior, preco_novo, custo_anterior, custo_novo) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, produtoId, precoAnterior, precoNovo, custoAnterior, custoNovo);
    }

    public List<HistoricoPreco> listarPorProduto(int produtoId) {
        String sql = "SELECT * FROM historico_preco_produto WHERE produto_id = ? ORDER BY data_alteracao DESC";
        return jdbcTemplate.query(sql, MAPPER, produtoId);
    }
}
