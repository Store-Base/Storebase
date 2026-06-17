package com.storebase.controller;

import com.storebase.model.Cliente;
import com.storebase.model.Comprovante;
import com.storebase.model.Funcionario;
import com.storebase.model.ItemVenda;
import com.storebase.model.Produto;
import com.storebase.model.Venda;
import com.storebase.service.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public List<Venda> listarTodas() {
        return vendaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(vendaService.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Venda> listarPorCliente(@PathVariable int clienteId) {
        return vendaService.listarPorCliente(clienteId);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarVenda(@RequestBody Map<String, Object> body) {
        Venda venda = new Venda();

        if (body.get("clienteId") != null) {
            Cliente cliente = new Cliente();
            cliente.setId(((Number) body.get("clienteId")).intValue());
            venda.setCliente(cliente);
        } else {
            Cliente avulso = new Cliente();
            avulso.setId(1);
            venda.setCliente(avulso);
        }

        Funcionario funcionario = new Funcionario();
        Object funcId = body.get("funcionarioId");
        funcionario.setId(funcId != null ? ((Number) funcId).intValue() : 1);
        venda.setFuncionario(funcionario);

        venda.setFormaPagamento((String) body.get("formaPagamento"));
        Object desc = body.get("desconto");
        venda.setDesconto(desc != null ? ((Number) desc).doubleValue() : 0);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itensJson = (List<Map<String, Object>>) body.get("itens");
        List<ItemVenda> itens = new ArrayList<>();
        for (Map<String, Object> itemJson : itensJson) {
            Produto produto = new Produto();
            produto.setId(((Number) itemJson.get("produtoId")).intValue());
            int qtd = ((Number) itemJson.get("quantidade")).intValue();
            itens.add(new ItemVenda(produto, qtd));
        }
        venda.setItens(itens);

        vendaService.registrarVenda(venda);

        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("id",    venda.getId());
        resp.put("total", venda.getValorTotal());
        return ResponseEntity.status(201).body(resp);
    }

    @GetMapping("/{id}/comprovante")
    public ResponseEntity<Comprovante> gerarComprovante(@PathVariable int id) {
        return ResponseEntity.ok(vendaService.gerarComprovante(id));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
