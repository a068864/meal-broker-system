FROM openjdk:22-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

ARG JAR_FILE=target/service-registry-1.0.0-SNAPSHOT.jar

# Copy the jar file
COPY ${JAR_FILE} app.jar

# Expose the port the service runs on
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]