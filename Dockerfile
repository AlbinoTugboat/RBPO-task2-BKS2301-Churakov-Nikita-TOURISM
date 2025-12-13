FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Копируем исходный код и конфигурации
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Даем права на выполнение mvnw
RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Финальный образ
FROM openjdk:17-jdk-slim

WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Копируем JAR из builder stage
COPY --from=builder /app/target/*.jar app.jar

# Копируем SSL сертификаты (опционально, если генерируем при запуске)
# COPY src/main/resources/ssl/ /app/ssl/

EXPOSE 8443 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]