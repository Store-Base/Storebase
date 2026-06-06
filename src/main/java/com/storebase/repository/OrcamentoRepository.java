package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.Orcamento;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrcamentoRepository {

    public void cadastrar(Orcamento orc) {
        String sql = "INSERT INTO orcamento (valor_total, status, cliente_id, usuario_id, nome_comprador, cpf_cnpj) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, orc.getValorTotal());
            stmt.setString(2, orc.getStatus());
            if (orc.getClienteId() > 0) {
                stmt.setInt(3, orc.getClienteId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setInt(4, orc.getUsuarioId());
            stmt.setString(5, orc.getNomeComprador());
            stmt.setString(6, orc.getCpfCnpj());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) orc.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar orcamento: " + e.getMessage());
        }
    }

    public List<Orcamento> listarTodos() {
        List<Orcamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM orcamento";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar orcamentos: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Orcamento> buscarPorId(int id) {
        String sql = "SELECT * FROM orcamento WHERE id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar orcamento por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Orcamento mapear(ResultSet rs) throws SQLException {
        Orcamento o = new Orcamento();
        o.setId(rs.getInt("id"));
        o.setValorTotal(rs.getDouble("valor_total"));
        o.setStatus(rs.getString("status"));
        o.setClienteId(rs.getInt("cliente_id"));
        o.setUsuarioId(rs.getInt("usuario_id"));
        o.setNomeComprador(rs.getString("nome_comprador"));
        o.setCpfCnpj(rs.getString("cpf_cnpj"));
        return o;
    }
}
