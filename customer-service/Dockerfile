FROM openjdk:22-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/customer-service-1.0.0-SNAPSHOT.jar customer-service.jar

# Expose the port the service runs on
EXPOSE 8083

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888
ENV SPRING_DATASOURCE_URL=jdbc:mysql://customer-db:3306/customerdb
ENV SPRING_DATASOURCE_USERNAME=customer_user
ENV SPRING_DATASOURCE_PASSWORD=customer_pass
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect

# Command to run the application
ENTRYPOINT ["java", "-jar", "customer-service.jar"]