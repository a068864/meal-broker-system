FROM openjdk:22-jdk-slim

LABEL maintainer="mealbroker-team@example.com"

WORKDIR /app

# Copy the jar file
COPY target/api-gateway-1.0.0-SNAPSHOT.jar api-gateway.jar

# Expose the port the service runs on
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888

# Command to run the application
ENTRYPOINT ["java", "-jar", "api-gateway.jar"]