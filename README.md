# Microservices Architecture Documentation

## Overview
This project follows a **microservices architecture** consisting of the following services:

1. **API Gateway Service** – Routes and balances requests among microservices.
2. **Hotel Service** – Manages hotel-related data.
3. **Rating Service** – Manages user ratings for hotels.
4. **User Service** – Manages user-related data.
5. **Service Registry** – Eureka Server for service discovery.
6. **Config Service** – Centralized configuration management using Spring Cloud Config.

Each service is independently deployed and communicates via **REST APIs**.

---

## Config Service
The **Config Service** is responsible for managing configurations centrally using **Spring Cloud Config** with a GitHub-based repository.

### Configuration
#### Application Properties (YAML Format)
```yaml
server:
  port: 8087

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

#### Dependencies (pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-config-server</artifactId>
    </dependency>
</dependencies>
```

#### Enable Config Server
Add `@EnableConfigServer` in the main application class.
```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

---

## API Gateway Service
The **API Gateway Service** acts as the single entry point for all client requests, routing them to the appropriate microservices using **Spring Cloud Gateway** and **Eureka Service Discovery**.

### Configuration
#### Application Properties (YAML Format)
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
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-config</artifactId>
    </dependency>
</dependencies>
```

---

## Other Microservices

### 1. **User Service**
#### Configuration
```yaml
server:
  port: 8081

spring:
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

### 2. **Hotel Service**
#### Configuration
```yaml
server:
  port: 8082

spring:
  application:
    name: Hotel-Service
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

### 3. **Rating Service**
#### Configuration
```yaml
server:
  port: 8083

spring:
  application:
    name: Rating-Service
  config:
    import: configServer: http://localhost:8087

eureka:
  client:
    register-with-eureka: true
    fetch-registry: true
    service-url:
      defaultZone: http://localhost:8085/eureka/
  instance:
    prefer-ip-address: true
```

### 4. **Service Registry (Eureka Server)**
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

#### Dependencies (pom.xml)
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
    </dependency>
</dependencies>
```

