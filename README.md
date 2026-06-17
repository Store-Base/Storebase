# StoreBase — Sistema de Gerenciamento de Loja

Sistema web para automação de operações comerciais, desenvolvido na disciplina de **POO2** da **UTFPR — Cornélio Procópio**.

O StoreBase centraliza o controle de uma loja em uma API REST com interface web, cobrindo cadastro de produtos e clientes, controle de estoque, fechamento de vendas, orçamentos (com conversão em venda) e relatórios gerenciais — com acesso diferenciado por perfil de usuário.

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Banco de Dados | PostgreSQL |
| Acesso a Dados | Spring JDBC (sem ORM) |
| Build | Maven |
| Frontend | HTML + CSS + JavaScript (vanilla, sem build) |
| Bibliotecas (CDN) | Chart.js, Lucide Icons, Toastify |

---

## Arquitetura

O projeto tem **duas partes independentes**:

```
Storebase/
├── src/main/java/com/storebase/      # Backend — API REST (Spring Boot)
│   ├── controller/                   # Camada HTTP (REST)
│   ├── service/                      # Regras de negócio
│   ├── repository/                   # Acesso ao banco (JDBC)
│   ├── model/                        # Entidades do domínio
│   └── config/                       # Conexão e CORS
├── src/main/resources/
│   ├── application.properties        # Configuração do banco
│   └── schema.sql                    # Criação das tabelas + dados de exemplo
└── storebase-spring/frontend/        # Frontend — site estático
    ├── index.html                    # Página única (SPA simples)
    ├── css/                          # Estilos
    └── js/                           # Lógica, roteamento e chamadas à API
```

- O **backend** roda em `http://localhost:8080` e expõe a API REST.
- O **frontend** é um site estático servido separadamente (não é empacotado pelo Spring). Ele consome a API a partir de `API_BASE` definido em `storebase-spring/frontend/js/api.js`.
- O schema do banco e os dados de exemplo são criados automaticamente a cada inicialização (de forma idempotente — não duplica).

---

## Pré-requisitos

- Java 17 ou superior (JDK)
- Maven 3.8+
- PostgreSQL 14+ rodando em `localhost:5432`

---

## Configuração e Execução

### 1. Crie o banco de dados

```sql
CREATE DATABASE storebase;
```

### 2. Configure as credenciais

Edite `src/main/resources/application.properties` com o usuário e a senha do seu PostgreSQL:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/storebase
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=org.postgresql.Driver

spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
spring.sql.init.continue-on-error=true
```

> O `schema.sql` cria as tabelas, os usuários de demonstração e popula produtos e clientes de exemplo na primeira execução.

### 3. Suba o backend

```bash
mvn spring-boot:run
```

A API ficará disponível em `http://localhost:8080`.

### 4. Abra o frontend

Abra o arquivo `storebase-spring/frontend/index.html` no navegador.

> Dica: para evitar problemas de cache durante o desenvolvimento, use um servidor estático (ex.: a extensão **Live Server** do VS Code) ou recarregue com `Ctrl+Shift+R`.

---

## Credenciais de Demonstração

A tela de login já vem com três usuários de exemplo, um para cada perfil:

| Login | Senha | Perfil |
|---|---|---|
| `leo.admin` | `admin123` | Administrador |
| `vini.vendas` | `vend123` | Vendedor |
| `car.estoque` | `est123` | Gerente de Estoque |

### Perfis de acesso

- **Administrador** — acesso completo: dashboard gerencial, produtos, clientes, funcionários, vendas, orçamentos, estoque e relatórios.
- **Vendedor** — dashboard de vendas, nova venda, clientes e orçamentos.
- **Gerente de Estoque** — dashboard de estoque, produtos e controle de estoque.

---

## Endpoints da API

### Autenticação / Funcionários — `/funcionarios`
| Método | Rota | Descrição |
|---|---|---|
| POST | `/funcionarios/autenticar` | Autentica o login (retorna dados do usuário + token) |
| GET | `/funcionarios` | Lista todos |
| GET | `/funcionarios/{id}` | Busca por ID |
| POST | `/funcionarios` | Cadastra novo |
| PUT | `/funcionarios/{id}` | Atualiza |
| DELETE | `/funcionarios/{id}` | Remove |

### Clientes — `/clientes`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/clientes` | Lista todos |
| GET | `/clientes/{id}` | Busca por ID |
| GET | `/clientes/buscar?nome=` | Busca por nome |
| GET | `/clientes/cpf/{cpf}` | Busca por CPF |
| GET | `/clientes/{id}/historico` | Histórico de compras |
| POST | `/clientes` | Cadastra novo |
| PUT | `/clientes/{id}` | Atualiza |
| DELETE | `/clientes/{id}` | Remove |

### Produtos — `/produtos`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/produtos` | Lista todos |
| GET | `/produtos/{id}` | Busca por ID |
| GET | `/produtos/buscar?nome=` | Busca por nome |
| GET | `/produtos/codigo/{codigo}` | Busca por código |
| GET | `/produtos/estoque-baixo?limite=` | Produtos com estoque abaixo do limite |
| POST | `/produtos` | Cadastra novo |
| PUT | `/produtos/{id}` | Atualiza |
| PATCH | `/produtos/{id}/estoque` | Registra entrada de estoque |
| DELETE | `/produtos/{id}` | Remove |

### Vendas — `/vendas`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/vendas?dataInicio=&dataFim=&formaPagamento=` | Lista vendas (com filtros opcionais) + totais do período |
| GET | `/vendas/{id}` | Detalhe da venda |
| GET | `/vendas/cliente/{clienteId}` | Vendas de um cliente |
| GET | `/vendas/{id}/comprovante` | Gera comprovante |
| POST | `/vendas` | Registra nova venda (baixa o estoque) |

### Orçamentos — `/orcamentos`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/orcamentos?status=` | Lista (filtro opcional: `ABERTO` / `FECHADO`) |
| GET | `/orcamentos/{id}` | Busca por ID |
| POST | `/orcamentos` | Cria novo |
| PUT | `/orcamentos/{id}` | Atualiza |
| POST | `/orcamentos/{id}/itens` | Adiciona item |
| DELETE | `/orcamentos/{id}/itens/{produtoId}` | Remove item |
| POST | `/orcamentos/{id}/converter` | Converte em venda |
| DELETE | `/orcamentos/{id}` | Remove |

### Estoque — `/estoque`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/estoque` | Posição de estoque + alertas |
| POST | `/estoque/entrada` | Registra entrada (soma à quantidade) |
| POST | `/estoque/ajuste` | Ajusta para uma quantidade exata |

### Dashboard — `/dashboard`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/dashboard/stats` | Indicadores gerais (admin) |
| GET | `/dashboard/grafico` | Vendas dos últimos 7 dias |
| GET | `/dashboard/ultimas-vendas` | Últimas vendas |
| GET | `/dashboard/stats-vendedor?funcId=` | Indicadores do vendedor |
| GET | `/dashboard/grafico-vendedor?funcId=` | Vendas do vendedor (7 dias) |
| GET | `/dashboard/minhas-vendas?funcId=` | Últimas vendas do vendedor |
| GET | `/dashboard/stats-estoque` | Indicadores de estoque |

### Relatórios — `/relatorios`
| Método | Rota | Descrição |
|---|---|---|
| GET | `/relatorios/vendas?dataInicio=&dataFim=` | Total, quantidade e vendas por forma de pagamento |
| GET | `/relatorios/produtos?dataInicio=&dataFim=` | Produtos vendidos no período |
| GET | `/relatorios/produtos-mais-vendidos?limite=` | Ranking de produtos |
| GET | `/relatorios/estoque` | Posição de estoque |
| GET | `/relatorios/clientes` | Clientes com total e quantidade de compras |
| GET | `/relatorios/faturamento?ano=` | Faturamento bruto e líquido por mês |

---

## Integrantes

- Leonardo Marino Scarparo Silva
- Vinicius Luiz Andretta Ferracini
- Arthur Henrique de Melo Almeida
