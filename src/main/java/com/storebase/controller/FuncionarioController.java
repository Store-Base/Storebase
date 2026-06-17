package com.storebase.controller;

import com.storebase.model.Funcionario;
import com.storebase.service.FuncionarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/funcionarios")
public class FuncionarioController {

    @Autowired
    private FuncionarioService funcionarioService;

    @GetMapping
    public List<Funcionario> listarTodos() {
        return funcionarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Funcionario> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(funcionarioService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Void> cadastrar(@RequestBody Funcionario funcionario) {
        funcionarioService.cadastrar(funcionario);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> atualizar(@PathVariable int id, @RequestBody Funcionario funcionario) {
        funcionario.setId(id);
        funcionarioService.atualizar(funcionario);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable int id) {
        funcionarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/autenticar")
    public ResponseEntity<Map<String, Object>> autenticar(@RequestBody Map<String, String> credenciais) {
        String login = credenciais.get("login");
        String senha = credenciais.get("senha");
        Funcionario funcionario = funcionarioService.autenticar(login, senha);

        String token = "token-" + funcionario.getId() + "-" + System.currentTimeMillis();

        Map<String, Object> resposta = new LinkedHashMap<>();
        resposta.put("id",    funcionario.getId());
        resposta.put("nome",  funcionario.getNome());
        resposta.put("cargo", funcionario.getCargo());
        resposta.put("token", token);

        return ResponseEntity.ok(resposta);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
    }
}
