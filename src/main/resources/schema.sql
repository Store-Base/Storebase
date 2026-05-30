CREATE TABLE IF NOT EXISTS cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255),
    endereco VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS funcionario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL,
    salario DECIMAL(10, 2)
);

CREATE TABLE IF NOT EXISTS produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    preco_venda DECIMAL(10, 2) NOT NULL,
    custo DECIMAL(10, 2) NOT NULL,
    quantidade_estoque INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS venda (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    valor_total DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2) NOT NULL DEFAULT 0,
    forma_pagamento VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    cliente_id INT NOT NULL,
    funcionario_id INT NOT NULL,
    CONSTRAINT fk_venda_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_venda_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

CREATE TABLE IF NOT EXISTS item_venda (
    venda_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (venda_id, produto_id),
    CONSTRAINT fk_item_venda FOREIGN KEY (venda_id) REFERENCES venda(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS orcamento (
    id SERIAL PRIMARY KEY,
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    cliente_id INT,
    funcionario_id INT NOT NULL,
    CONSTRAINT fk_orcamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_orcamento_funcionario FOREIGN KEY (funcionario_id) REFERENCES funcionario(id)
);

CREATE TABLE IF NOT EXISTS item_orcamento (
    orcamento_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (orcamento_id, produto_id),
    CONSTRAINT fk_item_orcamento FOREIGN KEY (orcamento_id) REFERENCES orcamento(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_orcamento_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);
