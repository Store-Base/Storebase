package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.Cliente;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, email, endereco, telefone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getEndereco());
            stmt.setString(5, cliente.getTelefone());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) cliente.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar cliente: " + e.getMessage());
        }
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome=?, cpf=?, email=?, endereco=?, telefone=? WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getEndereco());
            stmt.setString(5, cliente.getTelefone());
            stmt.setInt(6, cliente.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM cliente WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar cliente: " + e.getMessage());
        }
    }

    public Optional<Cliente> buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Cliente> listarTodos() {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente ORDER BY nome";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar clientes: " + e.getMessage());
        }
        return lista;
    }

    public List<Cliente> buscarPorNome(String nome) {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT * FROM cliente WHERE nome ILIKE ? ORDER BY nome";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar clientes por nome: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar cliente por CPF: " + e.getMessage());
        }
        return Optional.empty();
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        c.setTelefone(rs.getString("telefone"));
        return c;
    }
}
