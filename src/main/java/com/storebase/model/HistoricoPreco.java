package com.storebase.model;

import java.time.LocalDateTime;

public class HistoricoPreco {

    private int id;
    private int produtoId;
    private double precoAnterior;
    private double precoNovo;
    private double custoAnterior;
    private double custoNovo;
    private LocalDateTime dataAlteracao;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProdutoId() { return produtoId; }
    public void setProdutoId(int produtoId) { this.produtoId = produtoId; }

    public double getPrecoAnterior() { return precoAnterior; }
    public void setPrecoAnterior(double precoAnterior) { this.precoAnterior = precoAnterior; }

    public double getPrecoNovo() { return precoNovo; }
    public void setPrecoNovo(double precoNovo) { this.precoNovo = precoNovo; }

    public double getCustoAnterior() { return custoAnterior; }
    public void setCustoAnterior(double custoAnterior) { this.custoAnterior = custoAnterior; }

    public double getCustoNovo() { return custoNovo; }
    public void setCustoNovo(double custoNovo) { this.custoNovo = custoNovo; }

    public LocalDateTime getDataAlteracao() { return dataAlteracao; }
    public void setDataAlteracao(LocalDateTime dataAlteracao) { this.dataAlteracao = dataAlteracao; }
}
