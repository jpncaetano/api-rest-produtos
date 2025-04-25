# API REST - Gerenciamento de Produtos

## Descrição

API RESTful para gerenciamento de produtos, utilizando **Spring Boot**, **PostgreSQL**, **autenticação JWT** e boas práticas de desenvolvimento.

---

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL** (banco de dados relacional)
- **Spring Security + JWT** (autenticação e segurança)
- **JUnit 5 e Mockito** (testes automatizados)
- **Swagger UI** (documentação da API)
- **Docker e Docker Compose** (conteinerização da aplicação)

---

## 🧩 Estrutura do Projeto

```sh
src
├── main
│   ├── java
│   │   └── com
│   │       └── api
│   │           ├── ApiRestProdutosApplication.java
│   │           ├── controller
│   │           ├── dto
│   │           ├── enums
│   │           ├── exception
│   │           ├── model
│   │           ├── repository
│   │           ├── security
│   │           └── service
│   └── resources
│       ├── application.properties
│       └── application-test.properties
└── test
    └── java
        └── com
            └── api
                ├── controller
                ├── security
                └── service
```

---

## 📦 Requisitos para Rodar o Projeto

- **Java 21+**
- **Maven 3.8+**
- **PostgreSQL 15+**
- **Docker e Docker Compose** (opcional)

---

## ⚙️ Configuração Inicial

### 🔧 Sem Docker
1. Configure o PostgreSQL localmente.
2. Defina as variáveis de ambiente no `application.yml` ou `.env`:

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/api_db
DATABASE_USERNAME=seu_usuario
DATABASE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
SERVER_PORT=8080
```

### 🐳 Com Docker
1. Edite o arquivo `.env.example` e renomeie para `.env`.
2. Execute:

```sh
docker-compose up --build
```

A API estará disponível em `http://localhost:8080`.

---

## 📌 Funcionalidades

### 🔐 Autenticação
- `POST /auth/login` → Gera um token JWT.
- `POST /auth/register` → Registra um novo usuário.

### 🛍️ Produtos
- `GET /products` → Lista todos os produtos.
- `POST /products` → Cadastra um novo produto.
- `GET /products/{id}` → Busca um produto por ID.
- `PUT /products/{id}` → Atualiza um produto.
- `DELETE /products/{id}` → Remove um produto.

---

## 📚 Documentação da API

Acesse a documentação via Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

---

## ✅ Testes Automatizados

Execute os testes:

```sh
mvn test
mvn clean verify
mvn surefire-report:report
mvn site
```

---

## 🤝 Como Contribuir

1. Faça um fork do repositório.
2. Crie uma branch para a nova funcionalidade.
3. Envie commits claros e objetivos.
4. Faça push para o seu repositório.
5. Abra um Pull Request.

```sh
git clone [URL DO SEU FORK]
cd api-rest-produtos
git checkout -b feature/nova-funcionalidade

# Após alterações
git add .
git commit -m "Descrição clara da alteração"
git push origin sua-branch
```

---

## 📄 Licença

Este projeto está licenciado sob a licença MIT. Veja o arquivo `LICENSE` para mais informações.