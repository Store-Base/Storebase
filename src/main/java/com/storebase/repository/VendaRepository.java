package com.storebase.repository;

import com.storebase.model.Cliente;
import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Venda> vendaRowMapper = (rs, rowNum) -> {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setValorTotal(rs.getDouble("valor_total"));
        v.setDesconto(rs.getDouble("desconto"));
        v.setFormaPagamento(rs.getString("forma_pagamento"));
        v.setData(rs.getDate("data").toLocalDate());
        Cliente c = new Cliente();
        c.setId(rs.getInt("c_id"));
        c.setNome(rs.getString("c_nome"));
        c.setCpf(rs.getString("c_cpf"));
        c.setEmail(rs.getString("c_email"));
        c.setEndereco(rs.getString("c_endereco"));
        v.setCliente(c);
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("f_id"));
        f.setNome(rs.getString("f_nome"));
        f.setCargo(rs.getString("f_cargo"));
        f.setLogin(rs.getString("f_login"));
        v.setFuncionario(f);
        return v;
    };

    private static final String SELECT_VENDA =
        "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.data, " +
        "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
        "f.id AS f_id, f.nome AS f_nome, f.cargo AS f_cargo, f.login AS f_login " +
        "FROM vendas v " +
        "JOIN clientes c ON v.cliente_id = c.id " +
        "JOIN funcionarios f ON v.funcionario_id = f.id ";

    private List<ItemVenda> carregarItens(int vendaId) {
        return jdbcTemplate.query(
            "SELECT i.quantidade, i.subtotal, " +
            "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, p.categoria, p.quantidade_estoque " +
            "FROM itens_venda i JOIN produtos p ON i.produto_id = p.id " +
            "WHERE i.venda_id=?",
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
            vendaId
        );
    }

    public void salvar(Venda venda) {
        String sql = "INSERT INTO vendas (cliente_id, funcionario_id, valor_total, desconto, forma_pagamento, data) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, venda.getCliente().getId());
            ps.setInt(2, venda.getFuncionario().getId());
            ps.setDouble(3, venda.getValorTotal());
            ps.setDouble(4, venda.getDesconto());
            ps.setString(5, venda.getFormaPagamento());
            ps.setDate(6, Date.valueOf(venda.getData()));
            return ps;
        }, keyHolder);
        venda.setId(keyHolder.getKey().intValue());
        for (ItemVenda item : venda.getItens()) {
            jdbcTemplate.update(
                "INSERT INTO itens_venda (venda_id, produto_id, quantidade, subtotal) VALUES (?, ?, ?, ?)",
                venda.getId(), item.getProduto().getId(), item.getQuantidade(), item.getSubtotal()
            );
        }
    }

    public Optional<Venda> buscarPorId(int id) {
        List<Venda> result = jdbcTemplate.query(
            SELECT_VENDA + "WHERE v.id=?", vendaRowMapper, id
        );
        if (result.isEmpty()) return Optional.empty();
        Venda venda = result.get(0);
        venda.setItens(carregarItens(id));
        return Optional.of(venda);
    }

    public List<Venda> listarTodas() {
        List<Venda> lista = jdbcTemplate.query(SELECT_VENDA + "ORDER BY v.data DESC", vendaRowMapper);
        lista.forEach(v -> v.setItens(carregarItens(v.getId())));
        return lista;
    }

    public List<Venda> listarPorCliente(int clienteId) {
        List<Venda> lista = jdbcTemplate.query(
            SELECT_VENDA + "WHERE v.cliente_id=? ORDER BY v.data DESC",
            vendaRowMapper, clienteId
        );
        lista.forEach(v -> v.setItens(carregarItens(v.getId())));
        return lista;
    }
}
