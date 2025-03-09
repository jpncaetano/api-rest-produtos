# API REST - Gerenciamento de Produtos

## Descrição

API RESTful para gerenciamento de produtos, utilizando **Spring Boot**, **PostgreSQL**, **autenticação JWT** e boas práticas de desenvolvimento.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL** (banco de dados relacional)
- **Spring Security + JWT** (autenticação e segurança)
- **JUnit 5 e Mockito** (testes automatizados)
- **Swagger UI** (documentação da API)
- **Docker e Docker Compose** (conteinerização da aplicação)

## Estrutura do Projeto

```sh
src
├── main
│   ├── java
│   │   └── com
│   │       └── api
│   │           ├── ApiRestProdutosApplication.java
│   │           ├── controller
│   │           │   ├── AuthController.java
│   │           │   ├── ProductController.java
│   │           │   └── UserController.java
│   │           ├── dto
│   │           │   ├── AuthRequest.java
│   │           │   ├── AuthResponse.java
│   │           │   ├── ErrorResponse.java
│   │           │   ├── ProductDTO.java
│   │           │   └── UserDTO.java
│   │           ├── enums
│   │           │   └── Role.java
│   │           ├── EnvTest.java
│   │           ├── exception
│   │           │   ├── GlobalExceptionHandler.java
│   │           │   ├── ProductNotFoundException.java
│   │           │   ├── UserAlreadyExistsException.java
│   │           │   ├── UserNotAllowedException.java
│   │           │   └── UserNotFoundException.java
│   │           ├── model
│   │           │   ├── Product.java
│   │           │   └── User.java
│   │           ├── repository
│   │           │   ├── ProductRepository.java
│   │           │   └── UserRepository.java
│   │           ├── security
│   │           │   ├── CustomAuthenticationEntryPoint.java
│   │           │   ├── JwtAuthenticationFilter.java
│   │           │   ├── JwtUtil.java
│   │           │   └── SecurityConfig.java
│   │           └── service
│   │               ├── ProductService.java
│   │               ├── UserDetailsServiceImpl.java
│   │               └── UserService.java
│   └── resources
│       ├── application.properties
│       └── application-test.properties
└── test
    └── java
        └── com
            └── api
                ├── ApiRestProdutosApplicationTests.java
                ├── security
                │   └── JwtUtilTest.java
                ├── service
                │   ├── ProductServiceTest.java
                │   └── UserServiceTest.java
                └── controller
                    ├── AuthControllerTest.java
                    ├── ProductControllerTest.java
                    └── UserControllerTest.java
```

## Requisitos para Rodar o Projeto

- **Java 21+**
- **Maven 3.8+**
- **Docker e Docker Compose** (opcional)
- **PostgreSQL 15+**

## Configuração das Variáveis de Ambiente

Antes de rodar o projeto, configure as seguintes variáveis de ambiente:

```sh
DATABASE_URL=jdbc:postgresql://localhost:5432/api_db
DATABASE_USERNAME=seu_usuario
DATABASE_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
SERVER_PORT=8080

## Funcionalidades

- **Autenticação JWT** (Login e Registro de usuários)
- **Cadastro de Produtos** (CRUD completo)
- **Controle de Acesso por Roles** (Admin e Seller)
- **Persistência de Dados** com PostgreSQL
- **Configuração de Variáveis de Ambiente** (.env e Docker Compose)
- **Testes Automatizados** (JUnit e Mockito)
- **Documentação com Swagger UI**

Caso esteja usando Docker, edite o arquivo .env.example e renomeie para .env.
```

# Requisitos para Rodar o Projeto

### 🛠️ Pré-requisitos

- **Java 21+**
- **Maven 3.8+**
- **Docker e Docker Compose (opcional)**

---

## ⚙️ Configuração Inicial

### **Sem Docker**
1. Configure o PostgreSQL localmente.
2. Defina as variáveis de ambiente no arquivo `application.yml` ou crie um arquivo `.env`:

```env
JWT_SECRET=sua_chave_secreta
SERVER_PORT=8080

DB_URL=jdbc:postgresql://localhost:5432/api_produtos
DATABASE_USER=seu_usuario
DATABASE_PASSWORD=sua_senha
```

### **Com Docker**
1. Edite o arquivo `.env.example` fornecido no projeto e renomeie para `.env`.
2. Execute os containers:

```sh
docker-compose up --build
```

Após isso, a API estará acessível em: `http://localhost:8080`

---

## 📌 Funcionalidades

### 🔑 **Autenticação JWT**
- `POST /auth/login` → Gera um token JWT.
- `POST /auth/register` → Registra um novo usuário.

### 📦 **Produtos**
- `GET /products` → Lista todos os produtos.
- `POST /products` → Adiciona um novo produto.
- `GET /products/{id}` → Busca um produto pelo ID.
- `PUT /products/{id}` → Atualiza um produto existente.

---

### 🚧 Requisitos do Sistema
- Java 21+
- Maven 3.8+
- Docker (opcional)
- PostgreSQL

---

## 🔑 Autenticação JWT
- Endpoint `/auth/login` para gerar token JWT.
- Endpoint `/auth/register` para registrar novos usuários.

---

## 📚 Documentação da API

- Documentação disponível pelo Swagger UI em:

```
http://localhost:8080/swagger-ui.html
```

---

## ✅ Testes Automatizados

Execute testes e gere relatórios:

```sh
mvn test
mvn clean verify
mvn surefire-report:report
mvn surefire-report:report-only

# Cobertura de testes:
mvn test
mvn site
```

---

## 🌟 Como Contribuir

1. Faça um fork do repositório.
2. Crie uma branch para a sua contribuição.
3. Envie commits atômicos e claros.
4. Faça push das alterações para o seu repositório (`fork`).
5. Abra um Pull Request.

```sh
git clone [URL DO SEU FORK]
cd api-rest-produtos
git checkout -b feature/nova-funcionalidade

# Faça suas alterações
git add .
git commit -m "Descrição clara da alteração"
git push origin sua-branch
```

---

## 📜 Licença

Este projeto está licenciado sob a licença MIT. Para mais detalhes, consulte o arquivo `LICENSE` no repositório.

