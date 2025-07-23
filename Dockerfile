# Estágio 1: Build com Maven e JDK
# Usamos uma imagem com o JDK completo para compilar nosso código.
FROM eclipse-temurin:21-jdk-jammy AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia os arquivos de definição do Maven para o container
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Baixa as dependências do projeto. Isso otimiza o cache de layers do Docker.
RUN ./mvnw dependency:go-offline

# Copia o código fonte da aplicação
COPY src ./src

# Compila a aplicação e gera o JAR, pulando os testes que rodaremos separadamente
RUN ./mvnw package -DskipTests


# Estágio 2: Imagem Final de Execução
# Usamos uma imagem mínima, apenas com o Java Runtime Environment (JRE).
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copia o JAR gerado no estágio de build para a imagem final
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta que a aplicação usará
EXPOSE 8080

# Comando para iniciar a aplicação quando o container for executado
ENTRYPOINT ["java", "-jar", "app.jar"]