package com.storebase.repository;

import com.storebase.model.Funcionario;
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
public class FuncionarioRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Funcionario> rowMapper = (rs, rowNum) -> {
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("id"));
        f.setNome(rs.getString("nome"));
        f.setCargo(rs.getString("cargo"));
        f.setLogin(rs.getString("login"));
        f.setSenha(rs.getString("senha"));
        f.setSalario(rs.getDouble("salario"));
        return f;
    };

    public void salvar(Funcionario funcionario) {
        String sql = "INSERT INTO funcionarios (nome, cargo, login, senha, salario) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, funcionario.getNome());
            ps.setString(2, funcionario.getCargo());
            ps.setString(3, funcionario.getLogin());
            ps.setString(4, funcionario.getSenha());
            ps.setDouble(5, funcionario.getSalario());
            return ps;
        }, keyHolder);
        funcionario.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Funcionario funcionario) {
        jdbcTemplate.update(
            "UPDATE funcionarios SET nome=?, cargo=?, login=?, senha=?, salario=? WHERE id=?",
            funcionario.getNome(), funcionario.getCargo(), funcionario.getLogin(),
            funcionario.getSenha(), funcionario.getSalario(), funcionario.getId()
        );
    }

    public void deletar(int id) {
        jdbcTemplate.update("DELETE FROM funcionarios WHERE id=?", id);
    }

    public Optional<Funcionario> buscarPorId(int id) {
        List<Funcionario> result = jdbcTemplate.query(
            "SELECT * FROM funcionarios WHERE id=?", rowMapper, id
        );
        return result.stream().findFirst();
    }

    public List<Funcionario> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM funcionarios ORDER BY nome", rowMapper);
    }

    public Optional<Funcionario> buscarPorLogin(String login) {
        List<Funcionario> result = jdbcTemplate.query(
            "SELECT * FROM funcionarios WHERE login=?", rowMapper, login
        );
        return result.stream().findFirst();
    }
}
