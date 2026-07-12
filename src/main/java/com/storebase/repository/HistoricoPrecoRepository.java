package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.HistoricoPreco;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class HistoricoPrecoRepository {

    public void registrar(int produtoId, double precoAnterior, double precoNovo,
                           double custoAnterior, double custoNovo) {
        String sql = "INSERT INTO historico_preco_produto (produto_id, preco_anterior, preco_novo, custo_anterior, custo_novo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            stmt.setDouble(2, precoAnterior);
            stmt.setDouble(3, precoNovo);
            stmt.setDouble(4, custoAnterior);
            stmt.setDouble(5, custoNovo);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao registrar histórico de preço: " + e.getMessage());
        }
    }

    public List<HistoricoPreco> listarPorProduto(int produtoId) {
        List<HistoricoPreco> lista = new ArrayList<>();
        String sql = "SELECT * FROM historico_preco_produto WHERE produto_id = ? ORDER BY data_alteracao DESC";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, produtoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    HistoricoPreco h = new HistoricoPreco();
                    h.setId(rs.getInt("id"));
                    h.setProdutoId(rs.getInt("produto_id"));
                    h.setPrecoAnterior(rs.getDouble("preco_anterior"));
                    h.setPrecoNovo(rs.getDouble("preco_novo"));
                    h.setCustoAnterior(rs.getDouble("custo_anterior"));
                    h.setCustoNovo(rs.getDouble("custo_novo"));
                    h.setDataAlteracao(rs.getTimestamp("data_alteracao").toLocalDateTime());
                    lista.add(h);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar histórico de preço: " + e.getMessage());
        }
        return lista;
    }
}
