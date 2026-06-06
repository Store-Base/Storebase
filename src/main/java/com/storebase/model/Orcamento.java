package com.storebase.model;

public class Orcamento {

    private int id;
    private double valorTotal;
    private String status;
    private int clienteId;
    private int usuarioId;
    private String nomeComprador;
    private String cpfCnpj;

    public Orcamento() {
        this.status = "aberto";
    }

    public Orcamento(int clienteId, int usuarioId, String nomeComprador, String cpfCnpj) {
        this.clienteId = clienteId;
        this.usuarioId = usuarioId;
        this.nomeComprador = nomeComprador;
        this.cpfCnpj = cpfCnpj;
        this.status = "aberto";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getClienteId() { return clienteId; }
    public void setClienteId(int clienteId) { this.clienteId = clienteId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNomeComprador() { return nomeComprador; }
    public void setNomeComprador(String nomeComprador) { this.nomeComprador = nomeComprador; }

    public String getCpfCnpj() { return cpfCnpj; }
    public void setCpfCnpj(String cpfCnpj) { this.cpfCnpj = cpfCnpj; }
}
