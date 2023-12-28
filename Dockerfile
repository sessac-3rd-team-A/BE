# Dockerfile

# Use the specified platform image (in this case, linux/amd64)
# FROM openjdk:17-alpine
#
# # Set the JARFILE argument
# ARG JARFILE=build/libs/*.jar
#
# # Copy the JAR file into the container
# COPY ${JARFILE} app.jar
#
# # Set the entry point for the container
# ENTRYPOINT ["java", "-jar", "/app.jar"]

# Build Stage
FROM openjdk:17-alpine as builder
WORKDIR /app
COPY . /app
RUN ./gradlew build

# Final Stage
FROM openjdk:17-alpine
ARG JARFILE=build/libs/*.jar
COPY --from=builder /app/${JARFILE} /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]