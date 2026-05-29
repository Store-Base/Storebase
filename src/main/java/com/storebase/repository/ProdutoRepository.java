package com.storebase.repository;

import com.storebase.model.Produto;
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
public class ProdutoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Produto> rowMapper = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setCategoria(rs.getString("categoria"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return p;
    };

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, codigo, preco_venda, custo, categoria, quantidade_estoque) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, produto.getNome());
            ps.setString(2, produto.getCodigo());
            ps.setDouble(3, produto.getPrecoVenda());
            ps.setDouble(4, produto.getCusto());
            ps.setString(5, produto.getCategoria());
            ps.setInt(6, produto.getQuantidadeEstoque());
            return ps;
        }, keyHolder);
        produto.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Produto produto) {
        jdbcTemplate.update(
            "UPDATE produtos SET nome=?, codigo=?, preco_venda=?, custo=?, categoria=?, quantidade_estoque=? WHERE id=?",
            produto.getNome(), produto.getCodigo(), produto.getPrecoVenda(),
            produto.getCusto(), produto.getCategoria(), produto.getQuantidadeEstoque(),
            produto.getId()
        );
    }

    public void deletar(int id) {
        jdbcTemplate.update("DELETE FROM produtos WHERE id=?", id);
    }

    public Optional<Produto> buscarPorId(int id) {
        List<Produto> result = jdbcTemplate.query(
            "SELECT * FROM produtos WHERE id=?", rowMapper, id
        );
        return result.stream().findFirst();
    }

    public List<Produto> listarTodos() {
        return jdbcTemplate.query("SELECT * FROM produtos ORDER BY nome", rowMapper);
    }

    public List<Produto> buscarPorNome(String nome) {
        return jdbcTemplate.query(
            "SELECT * FROM produtos WHERE nome ILIKE ? ORDER BY nome",
            rowMapper, "%" + nome + "%"
        );
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        List<Produto> result = jdbcTemplate.query(
            "SELECT * FROM produtos WHERE codigo=?", rowMapper, codigo
        );
        return result.stream().findFirst();
    }
}
