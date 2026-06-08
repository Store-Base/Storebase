# StoreBase — Sistema de Gerenciamento de Loja

Sistema web para automação de operações comerciais, desenvolvido na disciplina de POO2 da UTFPR.

## Sobre o Projeto

O StoreBase centraliza o controle de uma loja em uma API REST, cobrindo desde o cadastro de produtos e clientes até o fechamento de vendas, conversão de orçamentos e geração de relatórios gerenciais.

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Banco de Dados | PostgreSQL |
| Acesso a Dados | JDBC (Spring JDBC) |
| Build | Maven |

## Pré-requisitos

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ rodando em `localhost:5432`

## Configuração e Execução

**1. Crie o banco de dados:**
```sql
CREATE DATABASE storebase;
```

**2. Configure as credenciais** criando o arquivo `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/storebase
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver

spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.continue-on-error=true
spring.jpa.defer-datasource-initialization=true
```

**3. Suba a aplicação:**
```bash
mvn spring-boot:run
```

O schema do banco é criado automaticamente na primeira execução. A API estará disponível em `http://localhost:8080`.

## Endpoints da API

### Funcionários `/funcionarios`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/funcionarios` | Lista todos |
| GET | `/funcionarios/{id}` | Busca por ID |
| POST | `/funcionarios` | Cadastra novo |
| PUT | `/funcionarios/{id}` | Atualiza |
| DELETE | `/funcionarios/{id}` | Remove |
| POST | `/funcionarios/autenticar` | Autentica login |

### Clientes `/clientes`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/clientes` | Lista todos |
| GET | `/clientes/{id}` | Busca por ID |
| GET | `/clientes/buscar?nome=` | Busca por nome |
| GET | `/clientes/cpf/{cpf}` | Busca por CPF |
| POST | `/clientes` | Cadastra novo |
| PUT | `/clientes/{id}` | Atualiza |
| DELETE | `/clientes/{id}` | Remove |
| GET | `/clientes/{id}/historico` | Histórico de compras |

### Produtos `/produtos`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/produtos` | Lista todos |
| GET | `/produtos/{id}` | Busca por ID |
| GET | `/produtos/buscar?nome=` | Busca por nome |
| GET | `/produtos/codigo/{codigo}` | Busca por código |
| GET | `/produtos/estoque-baixo` | Produtos com estoque baixo |
| POST | `/produtos` | Cadastra novo |
| PUT | `/produtos/{id}` | Atualiza |
| DELETE | `/produtos/{id}` | Remove |

### Vendas `/vendas`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/vendas` | Lista todas |
| GET | `/vendas/{id}` | Busca por ID |
| GET | `/vendas/cliente/{clienteId}` | Vendas por cliente |
| POST | `/vendas` | Registra nova venda |
| GET | `/vendas/{id}/comprovante` | Gera comprovante |

### Orçamentos `/orcamentos`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/orcamentos` | Lista todos |
| GET | `/orcamentos/{id}` | Busca por ID |
| POST | `/orcamentos` | Cria novo orçamento |
| POST | `/orcamentos/{id}/itens` | Adiciona item |
| DELETE | `/orcamentos/{id}/itens/{produtoId}` | Remove item |
| POST | `/orcamentos/{id}/converter` | Converte em venda |

### Relatórios `/relatorios`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/relatorios/vendas?inicio=&fim=` | Vendas por período |
| GET | `/relatorios/estoque` | Posição de estoque |
| GET | `/relatorios/faturamento?inicio=&fim=` | Faturamento bruto e líquido |
| GET | `/relatorios/produtos-mais-vendidos` | Ranking de produtos |

## Estrutura do Projeto

```
src/main/java/com/storebase/
├── controller/     # Camada HTTP (REST)
├── service/        # Regras de negócio
├── repository/     # Acesso ao banco (JDBC)
├── model/          # Entidades do domínio
└── config/         # Configuração de conexão
```

## Integrantes

- Leonardo Marino Scarparo Silva
- Vinicius Luiz Andretta Ferracini
- Arthur Henrique de Melo Almeida
