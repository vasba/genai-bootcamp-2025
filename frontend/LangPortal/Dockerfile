FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

RUN chmod +x ./gradlew
RUN ./gradlew wasmJsBrowserDistribution

EXPOSE 8090

CMD ["./gradlew", "wasmJsBrowserRun"]