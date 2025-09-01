FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY build/libs/resource-service-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
