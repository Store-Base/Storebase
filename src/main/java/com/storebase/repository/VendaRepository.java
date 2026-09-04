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

import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Venda> MAPPER = (rs, rowNum) -> {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setValorTotal(rs.getDouble("valor_total"));
        v.setDesconto(rs.getDouble("desconto"));
        v.setFormaPagamento(rs.getString("forma_pagamento"));
        v.setStatus(rs.getString("status"));
        v.setData(rs.getDate("data").toLocalDate());
        v.setParcelas(rs.getInt("parcelas"));
        v.setTaxaJuros(rs.getDouble("taxa_juros"));
        v.setObservacoes(rs.getString("observacoes"));
        int cId = rs.getInt("c_id");
        if (!rs.wasNull()) {
            Cliente c = new Cliente();
            c.setId(cId);
            c.setNome(rs.getString("c_nome"));
            c.setCpf(rs.getString("c_cpf"));
            c.setEmail(rs.getString("c_email"));
            c.setEndereco(rs.getString("c_endereco"));
            v.setCliente(c);
        }
        Funcionario f = new Funcionario();
        f.setId(rs.getInt("u_id"));
        f.setNome(rs.getString("u_nome"));
        f.setCargo(rs.getString("u_cargo"));
        f.setLogin(rs.getString("u_login"));
        v.setFuncionario(f);
        return v;
    };

    private static final RowMapper<ItemVenda> ITEM_MAPPER = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("p_id"));
        p.setNome(rs.getString("p_nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return new ItemVenda(p, rs.getInt("quantidade"));
    };

    private static final String SELECT_BASE =
            "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.status, v.data, v.parcelas, v.taxa_juros, v.observacoes, " +
            "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
            "u.id AS u_id, u.nome AS u_nome, u.cargo AS u_cargo, u.login AS u_login " +
            "FROM pedido v ";

    /**
     * Insere apenas a linha de pedido e devolve o id gerado. A transacao que
     * amarra pedido + itens fica em VendaService.registrarVenda (@Transactional).
     */
    public int inserirPedido(Venda venda) {
        String sql = "INSERT INTO pedido (valor_total, desconto, forma_pagamento, status, cliente_id, usuario_id, parcelas, taxa_juros, observacoes) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"});
            stmt.setDouble(1, venda.getValorTotal());
            stmt.setDouble(2, venda.getDesconto());
            stmt.setString(3, venda.getFormaPagamento());
            stmt.setString(4, venda.getStatus());
            if (venda.getCliente() != null) {
                stmt.setInt(5, venda.getCliente().getId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }
            stmt.setInt(6, venda.getFuncionario().getId());
            stmt.setInt(7, venda.getParcelas());
            stmt.setDouble(8, venda.getTaxaJuros());
            stmt.setString(9, venda.getObservacoes());
            return stmt;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void inserirItem(int vendaId, ItemVenda item) {
        String sql = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, vendaId, item.getProduto().getId(), item.getQuantidade(),
                item.getSubtotal() / item.getQuantidade());
    }

    public Optional<Venda> buscarPorId(int id) {
        String sql = SELECT_BASE +
                "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                "JOIN usuario u ON v.usuario_id = u.id " +
                "WHERE v.id = ?";
        Optional<Venda> venda = jdbcTemplate.query(sql, MAPPER, id).stream().findFirst();
        venda.ifPresent(v -> v.setItens(carregarItens(v.getId())));
        return venda;
    }

    public List<Venda> listarTodas() {
        String sql = SELECT_BASE +
                "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                "JOIN usuario u ON v.usuario_id = u.id " +
                "ORDER BY v.data DESC";
        List<Venda> lista = jdbcTemplate.query(sql, MAPPER);
        for (Venda v : lista) {
            v.setItens(carregarItens(v.getId()));
        }
        return lista;
    }

    public List<Venda> listarPorCliente(int clienteId) {
        String sql = SELECT_BASE +
                "JOIN cliente c ON v.cliente_id = c.id " +
                "JOIN usuario u ON v.usuario_id = u.id " +
                "WHERE v.cliente_id = ? ORDER BY v.data DESC";
        List<Venda> lista = jdbcTemplate.query(sql, MAPPER, clienteId);
        for (Venda v : lista) {
            v.setItens(carregarItens(v.getId()));
        }
        return lista;
    }

    private List<ItemVenda> carregarItens(int vendaId) {
        String sql = "SELECT i.quantidade, i.preco_unitario, " +
                "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, p.quantidade_estoque " +
                "FROM item_pedido i JOIN produto p ON i.produto_id = p.id " +
                "WHERE i.pedido_id = ?";
        return jdbcTemplate.query(sql, ITEM_MAPPER, vendaId);
    }
}
