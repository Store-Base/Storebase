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
    senha VARCHAR(255) NOT NULL,
    salario DECIMAL(10, 2) NOT NULL DEFAULT 0
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
    parcelas INT NOT NULL DEFAULT 1,
    taxa_juros DECIMAL(10, 2) NOT NULL DEFAULT 0,
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
ALTER TABLE cliente ADD COLUMN IF NOT EXISTS observacoes TEXT;
ALTER TABLE usuario ADD COLUMN IF NOT EXISTS salario DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE pedido ALTER COLUMN cliente_id DROP NOT NULL;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS desconto DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS forma_pagamento VARCHAR(50);
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS status VARCHAR(50);
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS parcelas INT NOT NULL DEFAULT 1;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS taxa_juros DECIMAL(10,2) NOT NULL DEFAULT 0;
ALTER TABLE pedido ADD COLUMN IF NOT EXISTS observacoes TEXT;

-- Usuários de demonstração (correspondem às credenciais exibidas na tela de login)
INSERT INTO usuario (nome, cargo, login, senha, salario) VALUES
    ('Leonardo Marino', 'ADMINISTRADOR',   'leo.admin',   'admin123', 5800.00),
    ('Vinicius Vendas', 'VENDEDOR',         'vini.vendas', 'vend123',  2800.00),
    ('Carlos Estoque',  'GERENTE_ESTOQUE',  'car.estoque', 'est123',   3200.00)
ON CONFLICT (login) DO NOTHING;

-- Garante o salário dos usuários demo mesmo se já existirem sem valor
UPDATE usuario SET salario = 5800.00 WHERE login = 'leo.admin'   AND salario = 0;
UPDATE usuario SET salario = 2800.00 WHERE login = 'vini.vendas' AND salario = 0;
UPDATE usuario SET salario = 3200.00 WHERE login = 'car.estoque' AND salario = 0;

-- Produtos de exemplo (alguns com estoque baixo/crítico para acionar os alertas)
INSERT INTO produto (nome, codigo, preco_venda, custo, categoria, quantidade_estoque) VALUES
    ('Notebook Dell Inspiron 15', 'NB-001', 3499.00, 2600.00, 'Computadores',  24),
    ('Mouse Logitech MX Master',  'MS-002',   89.90,   55.00, 'Periféricos',   45),
    ('Teclado Mecânico Redragon', 'TC-003',  249.90,  180.00, 'Periféricos',   18),
    ('Monitor LG 24" Full HD',    'MN-004',  899.00,  650.00, 'Monitores',     12),
    ('Headset Gamer HyperX',      'HS-005',  189.90,  130.00, 'Áudio',         32),
    ('HD Externo Seagate 1TB',    'HD-006',  279.90,  200.00, 'Armazenamento',  8),
    ('Webcam Logitech C920',      'WC-007',  159.90,  110.00, 'Acessórios',    15),
    ('Hub USB-C Anker 7-em-1',    'HB-008',  119.90,   80.00, 'Acessórios',     5),
    ('SSD Kingston 500GB',        'SS-009',  349.90,  260.00, 'Armazenamento',  3),
    ('Cadeira Gamer DXRacer',     'CG-010', 1299.00,  950.00, 'Cadeiras',       7)
ON CONFLICT (codigo) DO NOTHING;

-- Clientes de exemplo
INSERT INTO cliente (nome, cpf, email, endereco, telefone) VALUES
    ('Maria Silva',    '123.456.789-00', 'maria@email.com',    'Rua das Flores, 123 - Centro',          '(43) 99901-1111'),
    ('João Oliveira',  '234.567.890-11', 'joao@email.com',     'Av. Brasil, 456 - Jardim das Nações',   '(43) 99902-2222'),
    ('Ana Costa',      '345.678.901-22', 'ana@email.com',      'Rua XV de Novembro, 789 - Boa Vista',   '(43) 99903-3333'),
    ('Pedro Santos',   '456.789.012-33', 'pedro@email.com',    'Av. Paraná, 101 - Vila Nova',           '(43) 99904-4444'),
    ('Lucas Ferreira', '567.890.123-44', 'lucas@email.com',    'Rua Sete de Setembro, 202 - Centro',    '(43) 99905-5555'),
    ('Carla Mendes',   '678.901.234-55', 'carla@email.com',    'Rua Mal. Deodoro, 303 - Jardim Europa', '(43) 99906-6666'),
    ('Roberto Lima',   '789.012.345-66', 'roberto@email.com',  'Av. Santos Dumont, 404 - São João',     '(43) 99907-7777'),
    ('Fernanda Souza', '890.123.456-77', 'fernanda@email.com', 'Rua Tiradentes, 505 - Vila Operária',   '(43) 99908-8888')
ON CONFLICT (cpf) DO NOTHING;
