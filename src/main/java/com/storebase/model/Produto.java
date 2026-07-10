package com.storebase.model;

public class Produto {

    private int id;
    private String nome;
    private String codigo;
    private double precoVenda;
    private double custo;
    private String categoria;
    private int quantidadeEstoque;

    private double icms;      // percentual, ex: 18.0
    private double ipi;       // percentual
    private double pis;       // percentual
    private double cofins;    // percentual
    private String ncm;       // código, ex: "8471.30.19"
    private String cfop;      // código, ex: "5.101"
    private String cst;       // código, ex: "00"

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

    public double getIcms() { return icms; }
    public void setIcms(double icms) { this.icms = icms; }

    public double getIpi() { return ipi; }
    public void setIpi(double ipi) { this.ipi = ipi; }

    public double getPis() { return pis; }
    public void setPis(double pis) { this.pis = pis; }

    public double getCofins() { return cofins; }
    public void setCofins(double cofins) { this.cofins = cofins; }

    public String getNcm() { return ncm; }
    public void setNcm(String ncm) { this.ncm = ncm; }

    public String getCfop() { return cfop; }
    public void setCfop(String cfop) { this.cfop = cfop; }

    public String getCst() { return cst; }
    public void setCst(String cst) { this.cst = cst; }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", nome='" + nome + "', codigo='" + codigo
                + "', precoVenda=" + precoVenda + ", categoria='" + categoria
                + "', estoque=" + quantidadeEstoque + "}";
    }
}
