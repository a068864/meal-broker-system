FROM openjdk:17-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/config-server-1.0.0-SNAPSHOT.jar config-server.jar

# Expose the port the service runs on
EXPOSE 8888

# Environment variables for authentication
# In production, these should be passed at runtime
ENV SPRING_SECURITY_USER_NAME=configuser
ENV SPRING_SECURITY_USER_PASSWORD=configpassword

# Command to run the application
ENTRYPOINT ["java", "-jar", "config-server.jar"]