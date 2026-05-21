package com.storebase.model;

import java.util.ArrayList;
import java.util.List;

public class Orcamento {

    private int id;
    private Cliente cliente;
    private List<ItemVenda> itens;
    private double valorTotal;
    private String status;

    public Orcamento() {
        this.itens = new ArrayList<>();
        this.status = "aberto";
    }

    public Orcamento(Cliente cliente) {
        this.cliente = cliente;
        this.itens = new ArrayList<>();
        this.status = "aberto";
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        calcularTotal();
    }

    public void removerItem(ItemVenda item) {
        itens.remove(item);
        calcularTotal();
    }

    public void calcularTotal() {
        double soma = 0;
        for (ItemVenda item : itens) {
            soma += item.getSubtotal();
        }
        this.valorTotal = soma;
    }

    public void fechar() {
        this.status = "fechado";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Orcamento{id=" + id + ", cliente="
                + (cliente != null ? cliente.getNome() : "N/A")
                + ", total=" + valorTotal + ", status='" + status + "'}";
    }
}
