# Первый этап: сборка
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Копируем исходный код
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .
COPY src src

# Даем права на выполнение
RUN chmod +x mvnw

# Собираем приложение
RUN ./mvnw clean package -DskipTests

# Второй этап: финальный образ
FROM openjdk:17-slim

WORKDIR /app

# Создаем пользователя для безопасности
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

# Копируем JAR из первого этапа
COPY --from=builder /app/target/*.jar app.jar

# Пробрасываем порты
EXPOSE 8443
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]