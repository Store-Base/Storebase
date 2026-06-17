package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.ItemOrcamento;
import com.storebase.model.Orcamento;
import com.storebase.model.Produto;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class OrcamentoRepository {

    public void cadastrar(Orcamento orc) {
        Connection conn = AppConfig.getConnection();
        try {
            conn.setAutoCommit(false);

            String sqlOrc = "INSERT INTO orcamento (valor_total, status, cliente_id, usuario_id, nome_comprador, cpf_cnpj) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sqlOrc, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setDouble(1, orc.getValorTotal());
                stmt.setString(2, orc.getStatus());
                if (orc.getClienteId() > 0) {
                    stmt.setInt(3, orc.getClienteId());
                } else {
                    stmt.setNull(3, Types.INTEGER);
                }
                stmt.setInt(4, orc.getUsuarioId());
                stmt.setString(5, orc.getNomeComprador());
                stmt.setString(6, orc.getCpfCnpj());
                stmt.executeUpdate();
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) orc.setId(rs.getInt(1));
                }
            }

            String sqlItem = "INSERT INTO item_orcamento (orcamento_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
            for (ItemOrcamento item : orc.getItens()) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
                    stmt.setInt(1, orc.getId());
                    stmt.setInt(2, item.getProduto().getId());
                    stmt.setInt(3, item.getQuantidade());
                    stmt.setDouble(4, item.getPrecoUnitario());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ex) { System.err.println(ex.getMessage()); }
            System.err.println("Erro ao cadastrar orcamento: " + e.getMessage());
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException e) { System.err.println(e.getMessage()); }
        }
    }

    public List<Orcamento> listarTodos() {
        List<Orcamento> lista = new ArrayList<>();
        String sql = "SELECT * FROM orcamento ORDER BY id DESC";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Orcamento o = mapear(rs);
                o.setItens(mapearItens(o.getId()));
                lista.add(o);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar orcamentos: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Orcamento> buscarPorId(int id) {
        String sql = "SELECT * FROM orcamento WHERE id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Orcamento o = mapear(rs);
                    o.setItens(mapearItens(o.getId()));
                    return Optional.of(o);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar orcamento por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    private List<ItemOrcamento> mapearItens(int orcamentoId) {
        List<ItemOrcamento> itens = new ArrayList<>();
        String sql = "SELECT i.orcamento_id, i.quantidade, i.preco_unitario, " +
                     "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, " +
                     "p.categoria, p.quantidade_estoque " +
                     "FROM item_orcamento i JOIN produto p ON i.produto_id = p.id " +
                     "WHERE i.orcamento_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orcamentoId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Produto p = new Produto();
                    p.setId(rs.getInt("p_id"));
                    p.setNome(rs.getString("p_nome"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setPrecoVenda(rs.getDouble("preco_venda"));
                    p.setCusto(rs.getDouble("custo"));
                    p.setCategoria(rs.getString("categoria"));
                    p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));

                    ItemOrcamento item = new ItemOrcamento(
                            rs.getInt("orcamento_id"),
                            p,
                            rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario")
                    );
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar itens do orcamento: " + e.getMessage());
        }
        return itens;
    }

    public void adicionarItem(int orcamentoId, ItemOrcamento item) {
        String sql = "INSERT INTO item_orcamento (orcamento_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (orcamento_id, produto_id) DO UPDATE SET quantidade = EXCLUDED.quantidade, preco_unitario = EXCLUDED.preco_unitario";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orcamentoId);
            stmt.setInt(2, item.getProduto().getId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao adicionar item ao orcamento: " + e.getMessage());
        }
    }

    public void removerItem(int orcamentoId, int produtoId) {
        String sql = "DELETE FROM item_orcamento WHERE orcamento_id = ? AND produto_id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orcamentoId);
            stmt.setInt(2, produtoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao remover item do orcamento: " + e.getMessage());
        }
    }

    public void atualizarStatus(int id, String status) {
        String sql = "UPDATE orcamento SET status = ? WHERE id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar status do orcamento: " + e.getMessage());
        }
    }

    public void atualizarValorTotal(int orcamentoId, double valorTotal) {
        String sql = "UPDATE orcamento SET valor_total = ? WHERE id = ?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, valorTotal);
            stmt.setInt(2, orcamentoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar valor total do orcamento: " + e.getMessage());
        }
    }

    private Orcamento mapear(ResultSet rs) throws SQLException {
        Orcamento o = new Orcamento();
        o.setId(rs.getInt("id"));
        o.setValorTotal(rs.getDouble("valor_total"));
        o.setStatus(rs.getString("status"));
        o.setClienteId(rs.getInt("cliente_id"));
        o.setUsuarioId(rs.getInt("usuario_id"));
        o.setNomeComprador(rs.getString("nome_comprador"));
        o.setCpfCnpj(rs.getString("cpf_cnpj"));
        Timestamp ts = rs.getTimestamp("data");
        if (ts != null) o.setData(ts.toLocalDateTime().toLocalDate().toString());
        return o;
    }
}
