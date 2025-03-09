FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew

EXPOSE 8090

CMD ["./gradlew", "wasmJsBrowserRun"]