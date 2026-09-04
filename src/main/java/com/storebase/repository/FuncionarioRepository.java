package com.storebase.repository;

import com.storebase.model.Funcionario;
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
public class FuncionarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Funcionario> MAPPER = (rs, rowNum) -> {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCargo(rs.getString("cargo"));
        f.setLogin(rs.getString("login"));
        f.setSenha(rs.getString("senha"));
        f.setSalario(rs.getDouble("salario"));
        f.setAtivo(rs.getBoolean("ativo")); // Lendo o dado do banco e setando no objeto
        return f;
    };

    public void salvar(Funcionario funcionario) {
        String sql = "INSERT INTO usuario (nome, cargo, login, senha, salario) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCargo());
            stmt.setString(3, funcionario.getLogin());
            stmt.setString(4, funcionario.getSenha());
            stmt.setDouble(5, funcionario.getSalario());
            return stmt;
        }, keyHolder);
        funcionario.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Funcionario funcionario) {
        // SQL ajustado para incluir o 'ativo'
        String sql = "UPDATE usuario SET nome=?, cargo=?, login=?, senha=?, salario=?, ativo=? WHERE id=?";
        jdbcTemplate.update(sql,
                funcionario.getNome(), funcionario.getCargo(), funcionario.getLogin(),
                funcionario.getSenha(), funcionario.getSalario(), funcionario.isAtivo(),
                funcionario.getId());
    }

    public void deletar(int id) {
        // Soft Delete: em vez de DELETE, fazemos um UPDATE mudando para false
        jdbcTemplate.update("UPDATE usuario SET ativo = false WHERE id=?", id);
    }

    public Optional<Funcionario> buscarPorId(int id) {
        // Filtrando para trazer apenas usuários ativos
        String sql = "SELECT * FROM usuario WHERE id=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public List<Funcionario> listarTodos() {
        // Filtrando ativos antes de fazer o ORDER BY
        return jdbcTemplate.query("SELECT * FROM usuario WHERE ativo = true ORDER BY nome", MAPPER);
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        // Importante garantir que um funcionário inativo não consiga fazer login
        String sql = "SELECT * FROM usuario WHERE login=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, login).stream().findFirst();
    }
}
