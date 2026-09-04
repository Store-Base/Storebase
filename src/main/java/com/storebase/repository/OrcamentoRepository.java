package com.storebase.repository;

import com.storebase.model.ItemOrcamento;
import com.storebase.model.Orcamento;
import com.storebase.model.Produto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

@Repository
public class OrcamentoRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final RowMapper<Orcamento> MAPPER = (rs, rowNum) -> {
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
    };

    private static final RowMapper<ItemOrcamento> ITEM_MAPPER = (rs, rowNum) -> {
        Produto p = new Produto();
        p.setId(rs.getInt("p_id"));
        p.setNome(rs.getString("p_nome"));
        p.setCodigo(rs.getString("codigo"));
        p.setPrecoVenda(rs.getDouble("preco_venda"));
        p.setCusto(rs.getDouble("custo"));
        p.setCategoria(rs.getString("categoria"));
        p.setQuantidadeEstoque(rs.getInt("quantidade_estoque"));
        return new ItemOrcamento(
                rs.getInt("orcamento_id"),
                p,
                rs.getInt("quantidade"),
                rs.getDouble("preco_unitario"));
    };

    /**
     * Insere apenas o cabecalho do orcamento e devolve o id gerado. A transacao
     * que amarra orcamento + itens fica em OrcamentoService (@Transactional).
     */
    public int inserirOrcamento(Orcamento orc) {
        String sql = "INSERT INTO orcamento (valor_total, status, cliente_id, usuario_id, nome_comprador, cpf_cnpj) VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            PreparedStatement stmt = conn.prepareStatement(sql, new String[]{"id"});
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
            return stmt;
        }, keyHolder);
        return keyHolder.getKey().intValue();
    }

    public void atualizarOrcamento(Orcamento orc) {
        String sql = "UPDATE orcamento SET valor_total=?, cliente_id=?, usuario_id=?, nome_comprador=? WHERE id=?";
        Object clienteId = orc.getClienteId() > 0
                ? orc.getClienteId()
                : new SqlParameterValue(Types.INTEGER, null);
        jdbcTemplate.update(sql, orc.getValorTotal(), clienteId, orc.getUsuarioId(),
                orc.getNomeComprador(), orc.getId());
    }

    /** INSERT simples de item, usado dentro das transacoes de criar/atualizar. */
    public void inserirItem(int orcamentoId, ItemOrcamento item) {
        String sql = "INSERT INTO item_orcamento (orcamento_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, orcamentoId, item.getProduto().getId(),
                item.getQuantidade(), item.getPrecoUnitario());
    }

    public void removerItens(int orcamentoId) {
        jdbcTemplate.update("DELETE FROM item_orcamento WHERE orcamento_id = ?", orcamentoId);
    }

    public void removerOrcamento(int id) {
        jdbcTemplate.update("DELETE FROM orcamento WHERE id = ?", id);
    }

    public List<Orcamento> listarTodos() {
        List<Orcamento> lista = jdbcTemplate.query("SELECT * FROM orcamento ORDER BY id DESC", MAPPER);
        for (Orcamento o : lista) {
            o.setItens(mapearItens(o.getId()));
        }
        return lista;
    }

    public Optional<Orcamento> buscarPorId(int id) {
        Optional<Orcamento> orcamento = jdbcTemplate
                .query("SELECT * FROM orcamento WHERE id = ?", MAPPER, id)
                .stream().findFirst();
        orcamento.ifPresent(o -> o.setItens(mapearItens(o.getId())));
        return orcamento;
    }

    private List<ItemOrcamento> mapearItens(int orcamentoId) {
        String sql = "SELECT i.orcamento_id, i.quantidade, i.preco_unitario, " +
                     "p.id AS p_id, p.nome AS p_nome, p.codigo, p.preco_venda, p.custo, " +
                     "p.categoria, p.quantidade_estoque " +
                     "FROM item_orcamento i JOIN produto p ON i.produto_id = p.id " +
                     "WHERE i.orcamento_id = ?";
        return jdbcTemplate.query(sql, ITEM_MAPPER, orcamentoId);
    }

    public void adicionarItem(int orcamentoId, ItemOrcamento item) {
        String sql = "INSERT INTO item_orcamento (orcamento_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?) " +
                     "ON CONFLICT (orcamento_id, produto_id) DO UPDATE SET quantidade = EXCLUDED.quantidade, preco_unitario = EXCLUDED.preco_unitario";
        jdbcTemplate.update(sql, orcamentoId, item.getProduto().getId(),
                item.getQuantidade(), item.getPrecoUnitario());
    }

    public void removerItem(int orcamentoId, int produtoId) {
        jdbcTemplate.update("DELETE FROM item_orcamento WHERE orcamento_id = ? AND produto_id = ?",
                orcamentoId, produtoId);
    }

    public void atualizarStatus(int id, String status) {
        jdbcTemplate.update("UPDATE orcamento SET status = ? WHERE id = ?", status, id);
    }

    public void atualizarValorTotal(int orcamentoId, double valorTotal) {
        jdbcTemplate.update("UPDATE orcamento SET valor_total = ? WHERE id = ?", valorTotal, orcamentoId);
    }
}
