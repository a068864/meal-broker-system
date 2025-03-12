FROM openjdk:17-jdk-slim

LABEL maintainer="mealbroker-team@example.com"

WORKDIR /app

# Copy the jar file
COPY target/restaurant-service-1.0.0-SNAPSHOT.jar restaurant-service.jar

# Expose the port the service runs on
EXPOSE 8084

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888
ENV SPRING_DATASOURCE_URL=jdbc:mysql://restaurant-db:3306/restaurantdb
ENV SPRING_DATASOURCE_USERNAME=restaurant_user
ENV SPRING_DATASOURCE_PASSWORD=restaurant_pass
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect

# Command to run the application
ENTRYPOINT ["java", "-jar", "restaurant-service.jar"]