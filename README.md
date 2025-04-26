# API REST - Gerenciamento de Produtos

## Descrição

Projeto de API RESTful para **cadastro, gerenciamento e controle de estoque de Produtos**, com suporte a múltiplos perfis de usuários (**Clientes, Vendedores e Administradores**), cada um com permissões e funcionalidades específicas dentro da plataforma.

A aplicação permite registro e autenticação segura com JWT, controle de permissões baseado em roles, e operações de CRUD completas sobre os produtos.

Desenvolvida aplicando boas práticas de segurança, tratamento global de erros e exceções, validação de entradas, além de testes unitários e de integração para garantir robustez.

A API integra-se com banco de dados PostgreSQL e conta com documentação gerada automaticamente via Swagger. Todo o ambiente pode ser facilmente reproduzido usando Docker.

Este é um projeto pessoal desenvolvido com o objetivo de testar e aprimorar minhas competências técnicas nas tecnologias aplicadas.
Durante o desenvolvimento, busquei aplicar boas práticas de arquitetura de software, princípios de Clean Code e conceitos de DDD, além de seguir um processo de versionamento de código e organização de commits e PRs.
O projeto também explora conceitos de modularização de ambientes utilizando Profiles separados para testes e integrações reais com banco de dados PostgreSQL e conteinerização via Docker.

## Funcionalidades Principais

- Cadastro de usuários (Customer, Seller, Admin)
- Login e geração de token JWT
- CRUD de produtos (acesso controlado por roles)
- Controle de estoque de produtos
- Atualização de perfil e exclusão de conta
- Administração de usuários (listar, buscar e excluir)
- Documentação da API com Swagger
- Conteinerização completa da aplicação com Docker

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3
- Spring Data JPA
- Hibernate
- PostgreSQL
- Spring Security com JWT
- JUnit 5 e Mockito (Testes Automatizados)
- Swagger UI (Documentação de API)
- Docker e Docker Compose
- Maven


## Estrutura dos Endpoints e Permissões

### Produtos

- `GET /products` — Público (listar produtos paginados)
- `GET /products/{id}` — Público (detalhar produto)
- `POST /products` — SELLER, ADMIN (criar produto)
- `PUT /products/{id}` — SELLER (próprio produto), ADMIN (atualizar produto)
- `PATCH /products/{id}/stock` — SELLER, ADMIN (alterar estoque)
- `DELETE /products/{id}` — SELLER (próprio produto), ADMIN (excluir produto)
- `GET /products/mine` — SELLER, ADMIN (listar produtos do próprio usuário)

### Usuários

- `GET /users/me` — CUSTOMER, SELLER, ADMIN (dados do próprio perfil)
- `PUT /users/me` — CUSTOMER, SELLER, ADMIN (atualizar próprio perfil)
- `DELETE /users/me` — CUSTOMER, SELLER, ADMIN (excluir conta)
- `GET /users` — ADMIN (listar todos os usuários)
- `GET /users/{id}` — ADMIN (buscar usuário por ID)
- `DELETE /users/{id}` — ADMIN (excluir usuário)

### Autenticação

- `POST /auth/register` — Público (registrar CUSTOMER ou SELLER)
- `POST /auth/register/admin` — ADMIN (registrar novo ADMIN)
- `POST /auth/login` — Público (autenticação e geração de token JWT)
- `POST /auth/logout` — CUSTOMER, SELLER, ADMIN (logout)

## Permissões de Acesso e Regras de Negócio

### Visitante (não autenticado)

- Registrar conta (Customer ou Seller)
- Fazer login
- Listar produtos
- Consultar detalhes de produto

### Customer

- Atualizar perfil
- Excluir própria conta
- Listar produtos e detalhes
- Ordenar produtos por preço

### Seller

- Tudo que um Customer pode fazer
- Criar produtos
- Atualizar e excluir apenas os seus próprios produtos
- Gerenciar estoque de produtos cadastrados
- Consultar produtos cadastrados

### Admin

- Tudo que um Seller pode fazer
- Criar novos Admins
- Listar todos os usuários
- Buscar usuários pelo ID
- Excluir qualquer usuário
- Excluir qualquer produto

## Como Executar o Projeto

### Rodando localmente

1. Configure seu banco PostgreSQL localmente (porta 5432).
2. Copie o arquivo `.env.example` e renomeie para `.env`.
3. Ajuste as variáveis de ambiente:

```
DB_URL=jdbc:postgresql://localhost:5432/produtos_db
DB_USERNAME=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
```

4. Execute a aplicação pela IDE ou terminal:

```bash
mvn clean install
mvn spring-boot:run
```

### Rodando via Docker

1. Certifique-se que Docker e Docker Compose estão instalados.
2. Copie o arquivo `.env.example` e renomeie para `.env`.
3. Execute:

```bash
docker compose up --build
```

A API estará disponível em:

```
http://localhost:8080/swagger-ui/index.html
```

## Testes Automatizados

Execute os testes unitários:

```bash
mvn test
```

## Boas Práticas Adotadas

- Utilização de DTOs para evitar exposição direta de entidades
- Controle de autenticação e autorização com JWT
- Controle de ações baseado em Roles (Admin, Seller, Customer)
- Documentação automática da API com Swagger
- Segregação de responsabilidades: Controller, Service, Repository
- Conteinerização da aplicação e banco de dados com Docker Compose
- Utilização de Profiles (ambiente de teste separado)

---

## Estrutura do Projeto

```sh
api-rest-produtos
└── src
│    ├── main
│    │   ├── java
│    │   │   └── com
│    │   │       └── api
│    │   │           ├── ApiRestProdutosApplication.java
│    │   │           ├── controller
│    │   │           ├── dto
│    │   │           ├── enums
│    │   │           ├── exception
│    │   │           ├── model
│    │   │           ├── repository
│    │   │           ├── security
│    │   │           └── service
│    │   └── resources
│    │       ├── application.properties
│    │       └── application-test.properties
│    └── test
│        └── java
│            └── com
│                └── api
│                    ├── controller
│                    ├── security
│                    └── service
├── .dockerignore
├── .env.example
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## Licença

Este projeto está licenciado sob a licença MIT.