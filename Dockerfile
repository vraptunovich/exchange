# Используем базовый образ с JDK
FROM amazoncorretto:17

# Создаем рабочую директорию внутри контейнера
WORKDIR /app

# Копируем скомпилированный jar-файл в контейнер
COPY build/libs/ExchangeRates-1.0.jar app.jar

# Открываем порт 8080 (или любой другой, который использует ваше приложение)
EXPOSE 8080

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]