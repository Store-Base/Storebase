package com.storebase.model;

public class ItemVenda {

    private Produto produto;
    private int quantidade;
    private double subtotal;

    public ItemVenda() {}

    public ItemVenda(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.subtotal = produto.getPrecoVenda() * quantidade;
    }

    public void calcularSubtotal() {
        this.subtotal = produto.getPrecoVenda() * quantidade;
    }

    public Produto getProduto() { return produto; }
    public void setProduto(Produto produto) { this.produto = produto; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    // Aliases para o frontend
    public String getNomeProduto() { return produto != null ? produto.getNome() : null; }

    public double getPrecoUnitario() {
        return quantidade != 0 ? subtotal / quantidade : 0;
    }

    @Override
    public String toString() {
        return "ItemVenda{produto=" + produto.getNome() + ", quantidade=" + quantidade
                + ", subtotal=" + subtotal + "}";
    }
}
