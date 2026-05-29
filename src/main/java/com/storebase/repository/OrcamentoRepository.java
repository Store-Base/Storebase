package com.storebase.repository;

import com.storebase.model.Cliente;
import com.storebase.model.ItemVenda;
import com.storebase.model.Orcamento;
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
public class OrcamentoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Orcamento> orcamentoRowMapper = (rs, rowNum) -> {
        Orcamento o = new Orcamento();
        o.setId(rs.getInt("id"));
        o.setValorTotal(rs.getDouble("valor_total"));
        o.setStatus(rs.getString("status"));
        Cliente c = new Cliente();
        c.setId(rs.getInt("c_id"));
        c.setNome(rs.getString("c_nome"));
        c.setCpf(rs.getString("c_cpf"));
        c.setEmail(rs.getString("c_email"));
        c.setEndereco(rs.getString("c_endereco"));
        o.setCliente(c);
        return o;
    };

    private static final String SELECT_ORCAMENTO =
        "SELECT o.id, o.valor_total, o.status, " +
        "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco " +
        "FROM orcamentos o JOIN clientes c ON o.cliente_id = c.id ";

    private List<ItemVenda> carregarItens(int orcamentoId) {
        return jdbcTemplate.query(
            "SELECT i.quantidade, i.subtotal, " +
            "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, p.categoria, p.quantidade_estoque " +
            "FROM itens_orcamento i JOIN produtos p ON i.produto_id = p.id " +
            "WHERE i.orcamento_id=?",
            (rs, rowNum) -> {
                Produto p = new Produto();
                p.setId(rs.getInt("p_id"));
                p.setNome(rs.getString("p_nome"));
                p.setCodigo(rs.getString("codigo"));
                p.setPrecoVenda(rs.getDouble("preco_venda"));
                p.setCusto(rs.getDouble("custo"));
                p.setCategoria(rs.getString("categoria"));
                p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                ItemVenda item = new ItemVenda();
                item.setProduto(p);
                item.setQuantidade(rs.getInt("quantidade"));
                item.setSubtotal(rs.getDouble("subtotal"));
                return item;
            },
            orcamentoId
        );
    }

    private void salvarItens(int orcamentoId, List<ItemVenda> itens) {
        for (ItemVenda item : itens) {
            jdbcTemplate.update(
                "INSERT INTO itens_orcamento (orcamento_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)",
                orcamentoId, item.getProduto().getId(), item.getQuantidade(), item.getSubtotal()
            );
        }
    }

    public void salvar(Orcamento orcamento) {
        String sql = "INSERT INTO orcamentos (cliente_id, valor_total, status) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, orcamento.getCliente().getId());
            ps.setDouble(2, orcamento.getValorTotal());
            ps.setString(3, orcamento.getStatus());
            return ps;
        }, keyHolder);
        orcamento.setId(keyHolder.getKey().intValue());
        salvarItens(orcamento.getId(), orcamento.getItens());
    }

    public void atualizar(Orcamento orcamento) {
        jdbcTemplate.update(
            "UPDATE orcamentos SET valor_total=?, status=? WHERE id=?",
            orcamento.getValorTotal(), orcamento.getStatus(), orcamento.getId()
        );
        jdbcTemplate.update("DELETE FROM itens_orcamento WHERE orcamento_id=?", orcamento.getId());
        salvarItens(orcamento.getId(), orcamento.getItens());
    }

    public void deletar(int id) {
        jdbcTemplate.update("DELETE FROM orcamentos WHERE id=?", id);
    }

    public Optional<Orcamento> buscarPorId(int id) {
        List<Orcamento> result = jdbcTemplate.query(
            SELECT_ORCAMENTO + "WHERE o.id=?", orcamentoRowMapper, id
        );
        if (result.isEmpty()) return Optional.empty();
        Orcamento orcamento = result.get(0);
        orcamento.setItens(carregarItens(id));
        return Optional.of(orcamento);
    }

    public List<Orcamento> listarTodos() {
        List<Orcamento> lista = jdbcTemplate.query(SELECT_ORCAMENTO + "ORDER BY o.id", orcamentoRowMapper);
        lista.forEach(o -> o.setItens(carregarItens(o.getId())));
        return lista;
    }

    public List<Orcamento> listarPorCliente(int clienteId) {
        List<Orcamento> lista = jdbcTemplate.query(
            SELECT_ORCAMENTO + "WHERE o.cliente_id=? ORDER BY o.id",
            orcamentoRowMapper, clienteId
        );
        lista.forEach(o -> o.setItens(carregarItens(o.getId())));
        return lista;
    }
}
