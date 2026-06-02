FROM eclipse-temurin:25-jdk-alpine

WORKDIR /app

COPY build/libs/resource-publisher-*.jar app.jar

EXPOSE 8086

ENTRYPOINT ["java", "-jar", "app.jar"]
