package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.Cliente;
import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VendaRepository {

    public void salvar(Venda venda) {
        Connection conn = AppConfig.getConnection();
        try {
            conn.setAutoCommit(false);

            String sqlPedido = "INSERT INTO pedido (valor_total, desconto, forma_pagamento, status, cliente_id, usuario_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDouble(1, venda.getValorTotal());
                stmt.setDouble(2, venda.getDesconto());
                stmt.setString(3, venda.getFormaPagamento());
                stmt.setString(4, venda.getStatus());
                if (venda.getCliente() != null) {
                    stmt.setInt(5, venda.getCliente().getId());
                } else {
                    stmt.setNull(5, java.sql.Types.INTEGER);
                }
                stmt.setInt(6, venda.getFuncionario().getId());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) venda.setId(rs.getInt(1));
                }
            }

            String sqlItem = "INSERT INTO item_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
            for (ItemVenda item : venda.getItens()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
                    stmt.setInt(1, venda.getId());
                    stmt.setInt(2, item.getProduto().getId());
                    stmt.setInt(3, item.getQuantidade());
                    stmt.setDouble(4, item.getSubtotal() / item.getQuantidade());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
            System.err.println("Erro ao salvar venda: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { System.err.println(e.getMessage()); }
        }
    }

    public Optional<Venda> buscarPorId(int id) {
        String sql = "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.status, v.data, " +
                     "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
                     "u.id AS u_id, u.nome AS u_nome, u.cargo AS u_cargo, u.login AS u_login " +
                     "FROM pedido v " +
                     "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                     "JOIN usuario u ON v.usuario_id = u.id " +
                     "WHERE v.id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venda v = mapear(rs);
                    v.setItens(carregarItens(v.getId()));
                    return Optional.of(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar venda por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Venda> listarTodas() {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.status, v.data, " +
                     "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
                     "u.id AS u_id, u.nome AS u_nome, u.cargo AS u_cargo, u.login AS u_login " +
                     "FROM pedido v " +
                     "LEFT JOIN cliente c ON v.cliente_id = c.id " +
                     "JOIN usuario u ON v.usuario_id = u.id " +
                     "ORDER BY v.data DESC";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Venda v = mapear(rs);
                v.setItens(carregarItens(v.getId()));
                lista.add(v);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar vendas: " + e.getMessage());
        }
        return lista;
    }

    public List<Venda> listarPorCliente(int clienteId) {
        List<Venda> lista = new ArrayList<>();
        String sql = "SELECT v.id, v.valor_total, v.desconto, v.forma_pagamento, v.status, v.data, " +
                     "c.id AS c_id, c.nome AS c_nome, c.cpf AS c_cpf, c.email AS c_email, c.endereco AS c_endereco, " +
                     "u.id AS u_id, u.nome AS u_nome, u.cargo AS u_cargo, u.login AS u_login " +
                     "FROM pedido v " +
                     "JOIN cliente c ON v.cliente_id = c.id " +
                     "JOIN usuario u ON v.usuario_id = u.id " +
                     "WHERE v.cliente_id = ? ORDER BY v.data DESC";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venda v = mapear(rs);
                    v.setItens(carregarItens(v.getId()));
                    lista.add(v);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar vendas por cliente: " + e.getMessage());
        }
        return lista;
    }

    private List<ItemVenda> carregarItens(int vendaId) {
        List<ItemVenda> itens = new ArrayList<>();
        String sql = "SELECT i.quantidade, i.preco_unitario, " +
                     "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, p.quantidade_estoque " +
                     "FROM item_pedido i JOIN produto p ON i.produto_id = p.id " +
                     "WHERE i.pedido_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, vendaId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("p_id"));
                    p.setNome(rs.getString("p_nome"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setPrecoVenda(rs.getDouble("preco_venda"));
                    p.setCusto(rs.getDouble("custo"));
                    p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
                    ItemVenda item = new ItemVenda(p, rs.getInt("quantidade"));
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar itens da venda: " + e.getMessage());
        }
        return itens;
    }

    private Venda mapear(ResultSet rs) throws SQLException {
        Venda v = new Venda();
        v.setId(rs.getInt("id"));
        v.setValorTotal(rs.getDouble("valor_total"));
        v.setDesconto(rs.getDouble("desconto"));
        v.setFormaPagamento(rs.getString("forma_pagamento"));
        v.setStatus(rs.getString("status"));
        v.setData(rs.getDate("data").toLocalDate());
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
    }
}
