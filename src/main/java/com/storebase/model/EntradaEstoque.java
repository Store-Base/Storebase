package com.storebase.model;

public class EntradaEstoque {

    private int quantidade;
    private String tipo = "entrada";

    public EntradaEstoque() {}

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
