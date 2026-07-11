package com.storebase.repository;

import com.storebase.config.AppConfig;
import com.storebase.model.Produto;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProdutoRepository {

    public void salvar(Produto produto) {
        String sql = "INSERT INTO produto (nome, codigo, preco_venda, custo, categoria, quantidade_estoque, icms, ipi, pis, cofins, ncm, cfop, cst) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
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
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) produto.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome=?, codigo=?, preco_venda=?, custo=?, categoria=?, quantidade_estoque=?, icms=?, ipi=?, pis=?, cofins=?, ncm=?, cfop=?, cst=? WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
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
            stmt.setInt(14, produto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM produto WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao deletar produto: " + e.getMessage());
        }
    }

    public Optional<Produto> buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por id: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Produto> listarTodos() {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto ORDER BY nome";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        } catch (SQLException e) {
            System.err.println("Erro ao listar produtos: " + e.getMessage());
        }
        return lista;
    }

    public List<Produto> buscarPorNome(String nome) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE nome ILIKE ? ORDER BY nome";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produtos por nome: " + e.getMessage());
        }
        return lista;
    }

    public Optional<Produto> buscarPorCodigo(String codigo) {
        String sql = "SELECT * FROM produto WHERE codigo=?";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return Optional.of(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar produto por codigo: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Produto> listarEstoqueBaixo(int limite) {
        List<Produto> lista = new ArrayList<>();
        String sql = "SELECT * FROM produto WHERE quantidade_estoque <= ? ORDER BY quantidade_estoque ASC";
        try (Connection conn = AppConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limite);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar estoque baixo: " + e.getMessage());
        }
        return lista;
    }

    private Produto mapear(ResultSet rs) throws SQLException {
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
        return p;
    }
}
