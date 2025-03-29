# Microservices Architecture Documentation

## Overview
This project follows a microservices architecture consisting of the following services:

1. **API Gateway Service** – Routes and balances requests among microservices.
2. **Hotel Service** – Manages hotel-related data.
3. **Rating Service** – Manages user ratings for hotels.
4. **User Service** – Manages user-related data.
5. **Service Registry** – Eureka Server for service discovery.
6. **Config Service** – Centralized configuration management.

Each service is independently deployed and communicates via REST APIs.

---

## API Gateway Service
The **API Gateway Service** acts as the single entry point for all client requests, routing them to the appropriate microservices using **Spring Cloud Gateway** and **Eureka Service Discovery**.

### Configuration
#### Application Properties (YAML or YML Format)
```yaml
server:
  port: 8086

spring:
  application:
    name: api-gateway
  cloud:
    gateway:
      routes:
        - id: USERSERVICE
          uri: lb://user-Service
          predicates:
            - Path=/users/**

        - id: HOTELSERVICE
          uri: lb://Hotel-Service
          predicates:
            - Path=/hotels/**,/staffs/**

        - id: RATINGSERVICE
          uri: lb://Rating-Service
          predicates:
            - Path=/ratings/**

  config:
    import: configServer: http://localhost:8087

eureka:
  client:
    fetch-registry: true
    register-with-eureka: true
    service-url:
      defaultZone: http://localhost:8085/eureka/
  instance:
    prefer-ip-address: true
```

### Dependencies (pom.xml)
#### Required Dependencies
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-gateway</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
</dependencies>
```

### Dependency Management (Used in all services)
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-dependencies</artifactId>
            <version>${spring-cloud.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Properties (Used in all services)
```xml
<properties>
    <java.version>21</java.version>
    <spring-cloud.version>2024.0.1</spring-cloud.version>
</properties>
```

---

## Other Microservices
### 1. **User Service**
- Manages user information and authentication.
- Exposes endpoints under `/users/**`.

#### Configuration
```yaml
server:
  port: 8081

spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL8Dialect

  datasource:
    url: jdbc:mysql://localhost:3306/microservices
    username: root
    password: PASSWORD
    driver-class-name: com.mysql.cj.jdbc.Driver
  application:
    name: user-Service

  config:
    import: configServer: http://localhost:8087

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8085/eureka/
    register-with-eureka: true
    fetch-registry: true

  instance:
    prefer-ip-address: true
```

#### Dependencies
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-config</artifactId>
</dependency>
```

### 2. **Hotel Service**
- Manages hotel data like name, location, and availability.

#### Configuration
```yaml
spring:
  config:
    import: configServer: http://localhost:8087
```

### 3. **Rating Service**
- Handles user reviews and hotel ratings.

#### Configuration
```yaml
spring:
  config:
    import: configServer: http://localhost:8087
```

### 4. **Service Registry (Eureka Server)**
- Manages service discovery for microservices.

#### Configuration
```yaml
server:
  port: 8085

eureka:
  instance:
    hostname: localhost

  client:
    register-with-eureka: false
    fetch-registry: false
```

### 5. **Config Service** (Centralized Configuration Server)
- Stores all configuration files in a GitHub repository.

#### Configuration
```yaml
spring:
  application:
    name: Config-Server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/ShubhamPatel81/microservice-configuration-file
          clone-on-start: true
```

#### Enable Config Server
```java
@EnableConfigServer
@SpringBootApplication
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### How to Run
1. Start the **Config Service** on port **8087**.
2. Start the **Eureka Server** on port **8085**.
3. Start the **API Gateway** (`ApiGatewayService`) on port **8086**.
4. Start the other microservices:
   - **UserService**
   - **HotelService**
   - **RatingService**
5. Access APIs via the gateway using the routes:
   - `http://localhost:8086/users/**`
   - `http://localhost:8086/hotels/**`
   - `http://localhost:8086/staffs/**`
   - `http://localhost:8086/ratings/**`

