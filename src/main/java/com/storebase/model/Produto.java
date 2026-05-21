package com.storebase.model;

public class Produto {

    private int id;
    private String nome;
    private String codigo;
    private double precoVenda;
    private double custo;
    private String categoria;
    private int quantidadeEstoque;

    public Produto() {}

    public Produto(String nome, String codigo, double precoVenda, double custo,
                   String categoria, int quantidadeEstoque) {
        this.nome = nome;
        this.codigo = codigo;
        this.precoVenda = precoVenda;
        this.custo = custo;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public double getPrecoVenda() { return precoVenda; }
    public void setPrecoVenda(double precoVenda) { this.precoVenda = precoVenda; }

    public double getCusto() { return custo; }
    public void setCusto(double custo) { this.custo = custo; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', codigo='" + codigo
                + "', precoVenda=" + precoVenda + ", categoria='" + categoria
                + "', estoque=" + quantidadeEstoque + "}";
    }
}
