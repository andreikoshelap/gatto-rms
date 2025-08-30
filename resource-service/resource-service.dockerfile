FROM bellsoft/liberica-openjdk-alpine-musl:21.0.1
COPY /target/resource-service-1.0.0.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
