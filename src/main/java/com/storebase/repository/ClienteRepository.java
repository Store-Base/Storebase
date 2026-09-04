package com.storebase.repository;

import com.storebase.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Cliente> MAPPER = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        c.setTelefone(rs.getString("telefone"));
        c.setObservacoes(rs.getString("observacoes"));
        c.setAtivo(rs.getBoolean("ativo")); // Mapeando o status do banco para o objeto!
        return c;
    };

    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO cliente (nome, cpf, email, endereco, telefone, observacoes) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, cliente.getNome());
            stmt.setString(2, cliente.getCpf());
            stmt.setString(3, cliente.getEmail());
            stmt.setString(4, cliente.getEndereco());
            stmt.setString(5, cliente.getTelefone());
            stmt.setString(6, cliente.getObservacoes());
            return stmt;
        }, keyHolder);
        cliente.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Cliente cliente) {
        String sql = "UPDATE cliente SET nome = ?, cpf = ?, email = ?, endereco = ?, telefone = ?, observacoes = ?, ativo = ? WHERE id = ?";
        jdbcTemplate.update(sql,
                cliente.getNome(), cliente.getCpf(), cliente.getEmail(),
                cliente.getEndereco(), cliente.getTelefone(), cliente.getObservacoes(),
                cliente.isAtivo(), cliente.getId());
    }

    // Mantivemos o método deletar, mas agora ele faz o Soft Delete direto no banco por segurança!
    public void deletar(int id) {
        jdbcTemplate.update("UPDATE cliente SET ativo = false WHERE id=?", id);
    }

    public Optional<Cliente> buscarPorId(int id) {
        String sql = "SELECT * FROM cliente WHERE id=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public List<Cliente> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM cliente WHERE ativo = true", MAPPER);
    }

    public List<Cliente> buscarPorNome(String nome) {
        String sql = "SELECT * FROM cliente WHERE nome ILIKE ? AND ativo = true ORDER BY nome";
        return jdbcTemplate.query(sql, MAPPER, "%" + nome + "%");
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT * FROM cliente WHERE cpf=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, cpf).stream().findFirst();
    }
}
