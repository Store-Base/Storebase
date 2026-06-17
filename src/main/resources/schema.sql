CREATE TABLE IF NOT EXISTS cliente (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    endereco VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    cargo VARCHAR(100) NOT NULL,
    login VARCHAR(100) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS produto (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    preco_venda DECIMAL(10, 2) NOT NULL,
    custo DECIMAL(10, 2) NOT NULL,
    categoria VARCHAR(100),
    quantidade_estoque INT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS pedido (
    id SERIAL PRIMARY KEY,
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL,
    desconto DECIMAL(10, 2) NOT NULL DEFAULT 0,
    forma_pagamento VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    cliente_id INT NOT NULL,
    usuario_id INT NOT NULL,
    CONSTRAINT fk_pedido_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);

CREATE TABLE IF NOT EXISTS item_pedido (
    pedido_id INT NOT NULL,
    produto_id INT NOT NULL,
    quantidade INT NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    PRIMARY KEY (pedido_id, produto_id),
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_item_produto FOREIGN KEY (produto_id) REFERENCES produto(id)
);

CREATE TABLE IF NOT EXISTS orcamento (
    id SERIAL PRIMARY KEY,
    data TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valor_total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL,
    cliente_id INT,
    usuario_id INT NOT NULL,
    nome_comprador VARCHAR(255) NOT NULL,
    cpf_cnpj VARCHAR(20) NOT NULL,
    CONSTRAINT fk_orcamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_orcamento_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
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

ALTER TABLE cliente ADD COLUMN IF NOT EXISTS telefone VARCHAR(20);
ALTER TABLE pedido ALTER COLUMN cliente_id DROP NOT NULL;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS desconto DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS forma_pagamento VARCHAR(50);
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS status VARCHAR(50);
