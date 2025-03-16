FROM openjdk:17-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/service-registry-1.0.0-SNAPSHOT.jar app.jar

# Expose the port the service runs on
EXPOSE 8761

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]