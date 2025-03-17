FROM openjdk:17-jdk-slim

LABEL maintainer="Gary Kar"

WORKDIR /app

# Copy the jar file
COPY target/order-service-1.0.0-SNAPSHOT.jar order-service.jar

# Expose the port the service runs on
EXPOSE 8085

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://service-registry:8761/eureka/
ENV SPRING_CONFIG_IMPORT=optional:configserver:http://config-server:8888
ENV SPRING_DATASOURCE_URL=jdbc:mysql://order-db:3306/orderdb
ENV SPRING_DATASOURCE_USERNAME=order_user
ENV SPRING_DATASOURCE_PASSWORD=order_pass
ENV SPRING_DATASOURCE_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
ENV SPRING_JPA_DATABASE_PLATFORM=org.hibernate.dialect.MySQL8Dialect

# Command to run the application
ENTRYPOINT ["java", "-jar", "order-service.jar"]