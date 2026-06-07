package com.storebase.model;

import java.util.List;

public class Comprovante {

    private int numeroVenda;
    private String data;
    private String nomeCliente;
    private String nomeFuncionario;
    private List<ItemVenda> itens;
    private double desconto;
    private double valorTotal;
    private String formaPagamento;

    public Comprovante() {}

    public int getNumeroVenda() { return numeroVenda; }
    public void setNumeroVenda(int numeroVenda) { this.numeroVenda = numeroVenda; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public String getNomeFuncionario() { return nomeFuncionario; }
    public void setNomeFuncionario(String nomeFuncionario) { this.nomeFuncionario = nomeFuncionario; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }
}
