package com.storebase.repository;

import com.storebase.model.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ClienteRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Cliente> rowMapper = (rs, rowNum) -> {
        Cliente c = new Cliente();
        c.setId(rs.getInt("id"));
        c.setNome(rs.getString("nome"));
        c.setCpf(rs.getString("cpf"));
        c.setEmail(rs.getString("email"));
        c.setEndereco(rs.getString("endereco"));
        return c;
    };

    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes (nome, cpf, email, endereco) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, cliente.getNome());
            ps.setString(2, cliente.getCpf());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getEndereco());
            return ps;
        }, keyHolder);
        cliente.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Cliente cliente) {
        jdbcTemplate.update(
            "UPDATE clientes SET nome=?, cpf=?, email=?, endereco=? WHERE id=?",
            cliente.getNome(), cliente.getCpf(), cliente.getEmail(),
            cliente.getEndereco(), cliente.getId()
        );
    }

    public void deletar(int id) {
        jdbcTemplate.update("DELETE FROM clientes WHERE id=?", id);
    }

    public Optional<Cliente> buscarPorId(int id) {
        List<Cliente> result = jdbcTemplate.query(
            "SELECT * FROM clientes WHERE id=?", rowMapper, id
        );
        return result.stream().findFirst();
    }

    public List<Cliente> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM clientes ORDER BY nome", rowMapper);
    }

    public List<Cliente> buscarPorNome(String nome) {
        return jdbcTemplate.query(
            "SELECT * FROM clientes WHERE nome ILIKE ? ORDER BY nome",
            rowMapper, "%" + nome + "%"
        );
    }

    public Optional<Cliente> buscarPorCpf(String cpf) {
        List<Cliente> result = jdbcTemplate.query(
            "SELECT * FROM clientes WHERE cpf=?", rowMapper, cpf
        );
        return result.stream().findFirst();
    }
}
