FROM openjdk:17-slim AS builder

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

FROM openjdk:17-slim

COPY --from=builder /app.jar /app.jar

ENTRYPOINT ["java", "-Xmx512m", "-jar", "/app.jar"]
