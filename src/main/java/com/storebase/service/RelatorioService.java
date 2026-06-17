package com.storebase.service;

import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.repository.RelatorioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    @Autowired
    private RelatorioRepository relatorioRepository;

    public List<Venda> listarVendasPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de inicio nao pode ser posterior a data de fim.");
        }
        return relatorioRepository.listarVendasPorPeriodo(dataInicio, dataFim);
    }

    public List<Produto> listarEstoque() {
        return relatorioRepository.listarEstoque();
    }

    public Map<String, Double> calcularFaturamento(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de inicio nao pode ser posterior a data de fim.");
        }
        return relatorioRepository.calcularFaturamento(dataInicio, dataFim);
    }

    public List<Map<String, Object>> listarMaisVendidos(int limite) {
        if (limite <= 0) {
            throw new IllegalArgumentException("Limite deve ser maior que zero.");
        }
        return relatorioRepository.listarMaisVendidos(limite);
    }

    public List<Map<String, Object>> listarFaturamentoPorAno(int ano) {
        return relatorioRepository.listarFaturamentoPorAno(ano);
    }

    public List<Map<String, Object>> listarClientesComHistorico() {
        return relatorioRepository.listarClientesComHistorico();
    }

    public List<Map<String, Object>> listarProdutosPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new IllegalArgumentException("Data de inicio nao pode ser posterior a data de fim.");
        }
        return relatorioRepository.listarProdutosPorPeriodo(dataInicio, dataFim);
    }
}
