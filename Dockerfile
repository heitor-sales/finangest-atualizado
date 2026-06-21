# Etapa de construção
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Etapa de execução
FROM openjdk:17.0.1-jdk-slim
COPY --from=build target/*.jar app.jar

# Liberando a porta 8081 conforme application.properties
EXPOSE 8081

ENTRYPOINT ["java","-jar","app.jar"]