package com.storebase.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Venda {

    private int id;
    private Cliente cliente;
    private Funcionario funcionario;
    private List<ItemVenda> itens;
    private double valorTotal;
    private double desconto;
    private String formaPagamento;
    private String status;
    private LocalDate data;
    private int parcelas = 1;
    private double taxaJuros;

    public Venda() {
        this.itens = new ArrayList<>();
        this.data = LocalDate.now();
        this.status = "finalizada";
    }

    public Venda(Cliente cliente, Funcionario funcionario) {
        this.cliente = cliente;
        this.funcionario = funcionario;
        this.itens = new ArrayList<>();
        this.data = LocalDate.now();
        this.status = "finalizada";
    }

    public void adicionarItem(ItemVenda item) {
        itens.add(item);
        calcularTotal();
    }

    public void calcularTotal() {
        double soma = 0;
        for (ItemVenda item : itens) {
            soma += item.getSubtotal();
        }
        this.valorTotal = soma;
    }

    public void aplicarDesconto(double desconto) {
        this.desconto = desconto;
        this.valorTotal = this.valorTotal - desconto;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public List<ItemVenda> getItens() { return itens; }
    public void setItens(List<ItemVenda> itens) { this.itens = itens; }

    public double getValorTotal() { return valorTotal; }
    public void setValorTotal(double valorTotal) { this.valorTotal = valorTotal; }

    public double getDesconto() { return desconto; }
    public void setDesconto(double desconto) { this.desconto = desconto; }

    public String getFormaPagamento() { return formaPagamento; }
    public void setFormaPagamento(String formaPagamento) { this.formaPagamento = formaPagamento; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas > 0 ? parcelas : 1; }

    public double getTaxaJuros() { return taxaJuros; }
    public void setTaxaJuros(double taxaJuros) { this.taxaJuros = taxaJuros; }

    // Aliases para o frontend (estrutura achatada)
    public double getTotal() { return this.valorTotal; }

    public double getValorParcela() {
        return parcelas > 0 ? valorTotal / parcelas : valorTotal;
    }

    public String getDataHora() { return data != null ? data.toString() : null; }

    public String getClienteNome() {
        return cliente != null ? cliente.getNome() : "Avulso";
    }

    public String getFuncionarioNome() {
        return funcionario != null ? funcionario.getNome() : null;
    }
}
