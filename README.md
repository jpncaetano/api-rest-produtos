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
│ ├── java
│ │ └── com
│ │     └── api
│ │         ├── ApiRestProdutosApplication.java
│ │         ├── controller
│ │         │ ├── AuthController.java
│ │         │ ├── ProductController.java
│ │         │ └── UserController.java
│ │         ├── dto
│ │         │ ├── AuthRequest.java
│ │         │ ├── AuthResponse.java
│ │         │ ├── ErrorResponse.java
│ │         │ ├── ProductDTO.java
│ │         │ └── UserDTO.java
│ │         ├── enums
│ │         │ └── Role.java
│ │         ├── EnvTest.java
│ │         ├── exception
│ │         │ ├── GlobalExceptionHandler.java
│ │         │ ├── ProductNotFoundException.java
│ │         │ ├── UserAlreadyExistsException.java
│ │         │ ├── UserNotAllowedException.java
│ │         │ └── UserNotFoundException.java
│ │         ├── model
│ │         │ ├── Product.java
│ │         │ └── User.java
│ │         ├── repository
│ │         │ ├── ProductRepository.java
│ │         │ └── UserRepository.java
│ │         ├── security
│ │         │ ├── CustomAuthenticationEntryPoint.java
│ │         │ ├── JwtAuthenticationFilter.java
│ │         │ ├── JwtUtil.java
│ │         │ └── SecurityConfig.java
│ │         └── service
│ │             ├── ProductService.java
│ │             ├── UserDetailsServiceImpl.java
│ │             └── UserService.java
│ └── resources
│     ├── application.properties
│     └── application-test.properties
└── test
    └── java
        └── com
            └── api
                ├── ApiRestProdutosApplicationTests.java
                ├── security
                │ └── JwtUtilTest.java
                └── service
                    ├── ProductServiceTest.java
                    └── UserServiceTest.java

```


## Funcionalidades

- **Autenticação JWT** (Login e Registro de usuários)
- **Cadastro de Produtos** (CRUD completo)
- **Controle de Acesso por Roles** (Admin e Seller)
- **Persistência de Dados** com PostgreSQL
- **Configuração de Variáveis de Ambiente** (.env e Docker Compose)
- **Testes Automatizados** (JUnit e Mockito)
- **Documentação com Swagger UI**

## Como Executar o Projeto

### Com Docker (Recomendado)

1. **Crie o arquivo `.env`** com base no `.env.example`.
2. **Execute os containers**:
   ```sh
   docker-compose up --build
3.  **A API estará acessível em:** `http://localhost:8080`

### Sem Docker

1.  **Configure o PostgreSQL**
2.  **Defina as variáveis de ambiente** no `application.yml` ou `.env`
3.  **Execute a aplicação**:

    ```sh
    mvn spring-boot:run
    ```

## Documentação da API

Acesse a documentação interativa no Swagger UI:  
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Contato

Caso tenha dúvidas ou sugestões, entre em contato pelo GitHub.

---


**API REST para gerenciamento de produtos - Desenvolvido com Spring Boot**
