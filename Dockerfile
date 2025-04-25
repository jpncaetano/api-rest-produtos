# Usa uma imagem base com Java 21
FROM eclipse-temurin:21-jdk

# Define o diretório de trabalho no container
WORKDIR /app

# Copia o conteúdo do projeto para dentro do container
COPY . .

# Compila o projeto com Maven (sem rodar os testes)
RUN ./mvnw clean package -DskipTests

# Expõe a porta usada pelo Spring Boot
EXPOSE 8080

# Comando para iniciar o .jar da aplicação
CMD ["java", "-jar", "target/api-rest-produtos-0.0.1-SNAPSHOT.jar"]
