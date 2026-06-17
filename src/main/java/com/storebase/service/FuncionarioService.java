package com.storebase.service;

import com.storebase.model.Funcionario;
import com.storebase.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    public void cadastrar(Funcionario funcionario) {
        if (funcionario.getLogin() == null || funcionario.getLogin().isBlank()) {
            throw new IllegalArgumentException("Login do funcionário não pode ser vazio.");
        }
        if (funcionario.getSenha() == null || funcionario.getSenha().isBlank()) {
            throw new IllegalArgumentException("Senha do funcionário não pode ser vazia.");
        }
        funcionarioRepository.buscarPorLogin(funcionario.getLogin()).ifPresent(f -> {
            throw new IllegalArgumentException("Já existe um funcionário com o login: " + funcionario.getLogin());
        });
        funcionarioRepository.salvar(funcionario);
    }

    public void atualizar(Funcionario funcionario) {
        Funcionario existente = buscarPorId(funcionario.getId());
        // Mantém a senha atual quando o formulário não envia uma nova
        if (funcionario.getSenha() == null || funcionario.getSenha().isBlank()) {
            funcionario.setSenha(existente.getSenha());
        }
        funcionarioRepository.atualizar(funcionario);
    }

    public void deletar(int id) {
        buscarPorId(id);
        funcionarioRepository.deletar(id);
    }

    public Funcionario buscarPorId(int id) {
        return funcionarioRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado com id: " + id));
    }

    public List<Funcionario> listarTodos() {
        return funcionarioRepository.listarTodos();
    }

    public Funcionario autenticar(String login, String senha) {
        Funcionario funcionario = funcionarioRepository.buscarPorLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Login ou senha inválidos."));
        if (!funcionario.getSenha().equals(senha)) {
            throw new IllegalArgumentException("Login ou senha inválidos.");
        }
        return funcionario;
    }
}
