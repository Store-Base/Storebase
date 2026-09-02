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

As credenciais **não ficam no repositório**. O `application.properties` versionado foi
removido; no lugar dele há um `application.properties.example` com placeholders.

**2.1.** Copie o exemplo para o arquivo local (ele é ignorado pelo Git e nunca deve ser commitado):

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**2.2.** Defina as variáveis de ambiente com os dados do seu PostgreSQL. No Windows, via
PowerShell, no nível de usuário:

```powershell
[Environment]::SetEnvironmentVariable("DB_URL", "jdbc:postgresql://localhost:5432/storebase", "User")
[Environment]::SetEnvironmentVariable("DB_USER", "postgres", "User")
[Environment]::SetEnvironmentVariable("DB_PASSWORD", "sua-senha-aqui", "User")
```

**2.3.** Defina também o segredo de assinatura do JWT (`JWT_SECRET`). O valor **não vai
para o repositório** — gere um aleatório localmente:

```powershell
$bytes = New-Object byte[] 64
[Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($bytes)
[Environment]::SetEnvironmentVariable("JWT_SECRET", [Convert]::ToBase64String($bytes), "User")
```

Reabra o terminal e a IDE para que as variáveis sejam lidas. Na IntelliJ, uma alternativa
é definir as variáveis na configuração de execução do projeto.

> As três variáveis do banco (`DB_URL`, `DB_USER`, `DB_PASSWORD`) são lidas tanto pelo
> `application.properties` quanto pela classe `AppConfig`. A `JWT_SECRET` é lida pelo
> `application.properties` (`jwt.secret`); a validade do token vem de `jwt.expiration-ms`
> (padrão 8 h).

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

## Autenticação e autorização

O login em `POST /funcionarios/autenticar` devolve um **JWT assinado** (HS256), que o
frontend guarda e envia em cada requisição no header `Authorization: Bearer <token>`.
O backend valida a assinatura e a expiração em todas as rotas — não há mais token
"de fachada".

- **Rotas públicas:** apenas `POST /funcionarios/autenticar` e o preflight `OPTIONS`.
- **Sem token ou token expirado:** `401` (o frontend encerra a sessão).
- **Token válido, mas perfil sem acesso:** `403`.

Autorização por perfil (via `@PreAuthorize` em cada controller):

| Recurso | Perfis com acesso |
|---|---|
| `/produtos`, `/estoque` | ADMINISTRADOR, GERENTE_ESTOQUE |
| `/clientes`, `/orcamentos`, `/vendas` | ADMINISTRADOR, VENDEDOR |
| `/funcionarios` | ADMINISTRADOR |
| `/relatorios` | ADMINISTRADOR |
| `/dashboard/stats*`, `/dashboard/grafico`, `/dashboard/ultimas-vendas` | ADMINISTRADOR |
| `/dashboard/*-vendedor`, `/dashboard/minhas-vendas` | VENDEDOR |
| `/dashboard/stats-estoque` | GERENTE_ESTOQUE |

As rotas de dashboard do vendedor identificam o funcionário pelo **id contido no
token**, não por parâmetro — um vendedor não consegue ver os números de outro.

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
