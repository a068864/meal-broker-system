# MealBroker Data Seeding

This document explains how the data seeding works in the MealBroker project and how to use it for development and
testing.

## Overview

Data seeding automatically populates the databases with sample data during application startup in development and test
environments. This ensures that you have consistent test data available for development and testing.

## How It Works

The data seeders are implemented as Spring `CommandLineRunner` beans that run when the application starts. They are
configured to run only in the `dev` and `test` profiles, so they won't affect production data.

Each microservice has its own data seeder:

1. **Customer Service** - Seeds customer data including names, contact information, and locations
2. **Restaurant Service** - Seeds restaurant chains, branches, menus, and menu items
3. **Order Service** - Seeds sample orders (requires customer and restaurant data to be seeded first)

## Using the Data Seeders

### Setting Up Your Environment

1. Make sure your application properties include the dev profile:

   ```
   spring.profiles.active=dev
   ```

2. For Docker-based environments, add the environment variable:

   ```yaml
   environment:
     - SPRING_PROFILES_ACTIVE=dev
   ```

### Running the Application with Seeded Data

The seeding happens automatically when you start the services with the `dev` profile active. The seeders will only run
if the database is empty.

### Dependency Order

Because orders depend on customers and restaurants, you should start the services in this order:

1. Service Registry (Eureka)
2. Config Server
3. Customer Service
4. Restaurant Service
5. Order Service
6. Order Broker
7. Location Service
8. API Gateway

### Accessing Seeded Data

After running the seeders, the following data will be available:

#### Customers

- 8 customers with locations in major Canadian cities
- IDs from 1 to 8

#### Restaurants

- 3 restaurant chains (Burger Palace, Pizza Haven, Sushi Express)
- 9 branches across different cities
- Each menu has 7 different items

#### Orders

- 20 random orders with various statuses
- Each order has 1-4 order items

## Customizing Data Seeding

If you want to customize the seeded data:

1. Modify the appropriate seeder class in the corresponding service
2. Rebuild and restart the service

## Resetting Seeded Data

To reset the seeded data:

1. Stop all services
2. Delete the database files (if using file-based databases) or drop the tables
3. Restart the services with the `dev` profile active

For H2 in-memory databases, simply restarting the service will reset the data since the database is recreated on startup
when using `ddl-auto: create-drop`.

## Disabling Data Seeding

If you want to disable data seeding, you can:

1. Change the active profile to something other than `dev` or `test`
2. Or modify the seeder class to include a condition that prevents seeding

For example:

```java

@Bean
@Profile({"dev", "test"})
@ConditionalOnProperty(name = "app.seeding.enabled", havingValue = "true", matchIfMissing = true)
public CommandLineRunner seedData() {
    // Seeding logic
}
```

Then you can disable seeding by setting `app.seeding.enabled=false` in your properties.