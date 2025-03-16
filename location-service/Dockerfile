FROM openjdk:22-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/location-service-1.0.0-SNAPSHOT.jar location-service.jar

# Expose the port the service runs on
EXPOSE 8086

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888
ENV GOOGLE_MAPS_API_KEY=AIzaSyBBtFaDsEqt3JDc8xMvyvlEx85OpROdj7s

# Command to run the application
ENTRYPOINT ["java", "-jar", "location-service.jar"]