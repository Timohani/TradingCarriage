# Этап 1: Сборка приложения (Builder)
FROM gradle:8.4.0-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle --no-daemon clean build

# Этап 2: Запуск приложения
FROM eclipse-temurin:17.0.8.1_1-jre-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]