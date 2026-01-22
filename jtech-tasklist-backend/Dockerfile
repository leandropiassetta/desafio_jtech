FROM eclipse-temurin:21-jdk as builder
WORKDIR /app
COPY . .
RUN chmod +x gradlew && ./gradlew clean build -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
COPY --from=builder /app/build/libs/*.jar tasklist.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tasklist.jar"]
