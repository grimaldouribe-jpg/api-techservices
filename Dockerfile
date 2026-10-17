
# ==========================================
# TECHSERVICES PERU - EJERCICIO 1
# SPRING BOOT + JAVA 21 + RENDER
# ==========================================

# ETAPA 1: COMPILAR EL PROYECTO
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ETAPA 2: EJECUTAR SPRING BOOT
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]
