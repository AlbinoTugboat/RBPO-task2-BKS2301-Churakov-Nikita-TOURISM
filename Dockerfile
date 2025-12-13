# Первый этап: сборка
FROM eclipse-temurin:17-jdk-jammy AS builder

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

# Второй этап: финальный образ (JRE для уменьшения размера)
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Создаем пользователя для безопасности
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Копируем JAR из первого этапа
COPY --from=builder /app/target/*.jar app.jar

# Пробрасываем порты
EXPOSE 8443
EXPOSE 8080

# Команда запуска
ENTRYPOINT ["java", "-jar", "/app/app.jar"]