FROM openjdk:17-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/order-broker-1.0.0-SNAPSHOT.jar order-broker.jar

# Expose the port the service runs on
EXPOSE 8082

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888

# Command to run the application
ENTRYPOINT ["java", "-jar", "order-broker.jar"]