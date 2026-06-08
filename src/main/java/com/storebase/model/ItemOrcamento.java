package com.storebase.model;

public class ItemOrcamento {

    private int orcamentoId;
    private Produto produto;
    private int quantidade;
    private double precoUnitario;
    private double subtotal;

    public ItemOrcamento() {}

    public ItemOrcamento(int orcamentoId, Produto produto, int quantidade, double precoUnitario) {
        this.orcamentoId = orcamentoId;
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        calcularSubtotal();
    }

    public void calcularSubtotal() {
        this.subtotal = this.quantidade * this.precoUnitario;
    }

    public int getOrcamentoId() { return orcamentoId; }
    public void setOrcamentoId(int orcamentoId) { this.orcamentoId = orcamentoId; }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    @Override
    public String toString() {
        return "ItemOrcamento{orcamentoId=" + orcamentoId + ", produto=" + produto.getNome()
                + ", quantidade=" + quantidade + ", precoUnitario=" + precoUnitario
                + ", subtotal=" + subtotal + "}";
    }
}
