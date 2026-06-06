package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.Funcionario;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class FuncionarioRepository {

    public void salvar(Funcionario funcionario) {
        String sql = "INSERT INTO usuario (nome, cargo, login, senha) VALUES (?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setString(3, funcionario.getLogin());
            stmt.setString(4, funcionario.getSenha());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) funcionario.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar funcionario: " + e.getMessage());
        }
    }

    public void atualizar(Funcionario funcionario) {
        String sql = "UPDATE usuario SET nome=?, cargo=?, login=?, senha=? WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setString(3, funcionario.getLogin());
            stmt.setString(4, funcionario.getSenha());
            stmt.setInt(5, funcionario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionario: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar funcionario: " + e.getMessage());
        }
    }

    public Optional<Funcionario> buscarPorId(int id) {
        String sql = "SELECT * FROM usuario WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionario por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Funcionario> listarTodos() {
        List<Funcionario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuario ORDER BY nome";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar funcionarios: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuario WHERE login=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar funcionario por login: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCargo(rs.getString("cargo"));
        f.setLogin(rs.getString("login"));
        f.setSenha(rs.getString("senha"));
        return f;
    }
}
