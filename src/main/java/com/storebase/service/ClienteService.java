package com.storebase.service;

import com.storebase.model.Cliente;
import com.storebase.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public void cadastrar(Cliente cliente) {
        if (cliente.getCpf() == null || cliente.getCpf().isBlank()) {
            throw new IllegalArgumentException("CPF do cliente não pode ser vazio.");
        }
        clienteRepository.buscarPorCpf(cliente.getCpf()).ifPresent(c -> {
            throw new IllegalArgumentException("Já existe um cliente com o CPF: " + cliente.getCpf());
        });
        clienteRepository.salvar(cliente);
    }

    public void atualizar(Cliente cliente) {
        buscarPorId(cliente.getId());
        clienteRepository.atualizar(cliente);
    }

    public void deletar(int id) {
        buscarPorId(id);
        clienteRepository.deletar(id);
    }

    public Cliente buscarPorId(int id) {
        return clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com id: " + id));
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listarTodos();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return clienteRepository.buscarPorNome(nome);
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.buscarPorCpf(cpf)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com CPF: " + cpf));
    }
}
