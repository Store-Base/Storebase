package com.storebase.repository;

import com.storebase.model.Produto;
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
public class ProdutoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Produto> MAPPER = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setCategoria(rs.getString("categoria"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        p.setIcms(rs.getDouble("icms"));
        p.setIpi(rs.getDouble("ipi"));
        p.setPis(rs.getDouble("pis"));
        p.setCofins(rs.getDouble("cofins"));
        p.setNcm(rs.getString("ncm"));
        p.setCfop(rs.getString("cfop"));
        p.setCst(rs.getString("cst"));
        p.setAtivo(rs.getBoolean("ativo")); // Mapeamento da nova coluna!
        return p;
    };

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, codigo, preco_venda, custo, categoria, quantidade_estoque, icms, ipi, pis, cofins, ncm, cfop, cst) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getCodigo());
            stmt.setDouble(3, produto.getPrecoVenda());
            stmt.setDouble(4, produto.getCusto());
            stmt.setString(5, produto.getCategoria());
            stmt.setInt(6, produto.getQuantidadeEstoque());
            stmt.setDouble(7, produto.getIcms());
            stmt.setDouble(8, produto.getIpi());
            stmt.setDouble(9, produto.getPis());
            stmt.setDouble(10, produto.getCofins());
            stmt.setString(11, produto.getNcm());
            stmt.setString(12, produto.getCfop());
            stmt.setString(13, produto.getCst());
            return stmt;
        }, keyHolder);
        produto.setId(keyHolder.getKey().intValue());
    }

    public void atualizar(Produto produto) {
        // Coluna 'ativo' adicionada no UPDATE!
        String sql = "UPDATE produto SET nome=?, codigo=?, preco_venda=?, custo=?, categoria=?, quantidade_estoque=?, icms=?, ipi=?, pis=?, cofins=?, ncm=?, cfop=?, cst=?, ativo=? WHERE id=?";
        jdbcTemplate.update(sql,
                produto.getNome(), produto.getCodigo(), produto.getPrecoVenda(), produto.getCusto(),
                produto.getCategoria(), produto.getQuantidadeEstoque(), produto.getIcms(), produto.getIpi(),
                produto.getPis(), produto.getCofins(), produto.getNcm(), produto.getCfop(), produto.getCst(),
                produto.isAtivo(), produto.getId());
    }

    public void deletar(int id) {
        // Soft delete no produto!
        jdbcTemplate.update("UPDATE produto SET ativo = false WHERE id=?", id);
    }

    public Optional<Produto> buscarPorId(int id) {
        // Filtro ativo = true
        String sql = "SELECT * FROM produto WHERE id=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
    }

    public List<Produto> listarTodos() {
        // Filtro ativo = true
        return jdbcTemplate.query("SELECT * FROM produto WHERE ativo = true ORDER BY nome", MAPPER);
    }

    public List<Produto> buscarPorNome(String nome) {
        // Filtro ativo = true
        String sql = "SELECT * FROM produto WHERE nome ILIKE ? AND ativo = true ORDER BY nome";
        return jdbcTemplate.query(sql, MAPPER, "%" + nome + "%");
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        // Filtro ativo = true
        String sql = "SELECT * FROM produto WHERE codigo=? AND ativo = true";
        return jdbcTemplate.query(sql, MAPPER, codigo).stream().findFirst();
    }

    public List<Produto> listarEstoqueBaixo(int limite) {
        // Filtro ativo = true
        String sql = "SELECT * FROM produto WHERE quantidade_estoque <= ? AND ativo = true ORDER BY quantidade_estoque ASC";
        return jdbcTemplate.query(sql, MAPPER, limite);
    }
}
