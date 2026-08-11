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
        String sql = "INSERT INTO usuario (nome, cargo, login, senha, salario) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setString(3, funcionario.getLogin());
            stmt.setString(4, funcionario.getSenha());
            stmt.setDouble(5, funcionario.getSalario());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) funcionario.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar funcionario: " + e.getMessage());
        }
    }

    public void atualizar(Funcionario funcionario) {
        // SQL ajustado para incluir o 'ativo'
        String sql = "UPDATE usuario SET nome=?, cargo=?, login=?, senha=?, salario=?, ativo=? WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setString(3, funcionario.getLogin());
            stmt.setString(4, funcionario.getSenha());
            stmt.setDouble(5, funcionario.getSalario());
            stmt.setBoolean(6, funcionario.isAtivo()); // Posição 6: status ativo
            stmt.setInt(7, funcionario.getId());       // Posição 7: id para o WHERE
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar funcionario: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        // Soft Delete: em vez de DELETE, fazemos um UPDATE mudando para false
        String sql = "UPDATE usuario SET ativo = false WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao desativar funcionario: " + e.getMessage());
        }
    }

    public Optional<Funcionario> buscarPorId(int id) {
        // Filtrando para trazer apenas usuários ativos
        String sql = "SELECT * FROM usuario WHERE id=? AND ativo = true";
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
        // Filtrando ativos antes de fazer o ORDER BY
        String sql = "SELECT * FROM usuario WHERE ativo = true ORDER BY nome";
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
        // Importante garantir que um funcionário inativo não consiga fazer login
        String sql = "SELECT * FROM usuario WHERE login=? AND ativo = true";
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
        f.setSalario(rs.getDouble("salario"));
        f.setAtivo(rs.getBoolean("ativo")); // Lendo o dado do banco e setando no objeto
        return f;
    }
}