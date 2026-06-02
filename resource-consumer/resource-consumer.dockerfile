FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY build/libs/resource-consumer-*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
