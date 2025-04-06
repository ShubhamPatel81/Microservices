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




# This is register this service to the service Registry as a client
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8085/eureka/
    fetch-registry: true
    register-with-eureka: true
  instance:
    prefer-ip-address: true

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




### 6. **Feign Client Integration**
This project uses **Spring Cloud OpenFeign** to simplify inter-service communication in a microservices architecture. Feign allows services to communicate with each other declaratively using Java interfaces.


#### Dependency

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

#### Enable Feign Clients

```java
@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

#### Example Feign Client

```java
@FeignClient(name = "HOTEL-SERVICE")
public interface HotelServiceClient {

    @GetMapping("/hotels/{hotelId}")
    Hotel getHotelById(@PathVariable String hotelId);
}
```

#### Usage

```java
@Autowired
private HotelServiceClient hotelServiceClient;

public Hotel fetchHotel(String hotelId) {
    return hotelServiceClient.getHotelById(hotelId);
}
```






### 7. **Resilience4j Retry, CircuitBreaker, RateLimiter Integration**

This implements **Resilience4j Retry** to handle transient faults by retrying failed external service calls. A fallback method is provided in case of persistent failure.

###  Dependencies

```xml
<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<!-- AOP (Required for Resilience4j to work) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-aop</artifactId>
</dependency>

<!-- Resilience4j -->
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

### Controller Example

```java
int retryCount = 1;

@GetMapping("/{userId}")
// @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallBack")
@Retry(name = "ratingHotelService", fallbackMethod = "ratingHotelFallBack")
public ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId) {

    logger.info("Retry count: {}", retryCount);
    retryCount++;

    User user = userService.getUser(userId);
    return ResponseEntity.ok(user);
}
```

###  Fallback Method(Used in all @RateLimiter, @Retry,@CircuitBreaker)

```java
public ResponseEntity<User> ratingHotelFallBack(String userId, Exception ex) {
    System.out.println("Fallback executed due to service failure: " + ex.getMessage());

    User user = User.builder()
            .userId("121")
            .name("dummy")
            .email("dummy@gmail.com")
            .about("This user is created because some service is down !!!")
            .build();

    return new ResponseEntity<>(user, HttpStatus.OK);
}
```

---

### **Resilience4j Circuit Breaker Configuration**

Enables Circuit Breaker pattern to prevent system overload due to repeated service failures.

###  `application.yml` Configuration

```yaml
# Resilience4j + Actuator Configuration
management:
  health:
    circuitbreakers:
      enabled: true

  endpoint:
    health:
      show-details: always

  endpoints:
    web:
      exposure:
        include: health

resilience4j:
  circuitbreaker:
    instances:
      ratingHotelBreaker:
        register-health-indicator: true
        event-consumer-buffer-size: 10
        failure-rate-threshold: 50
        minimum-number-of-calls: 5
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 5s
        permitted-number-of-calls-in-half-open-state: 3
        sliding-window-size: 10
        sliding-window-type: COUNT_BASED

  retry:
    instances:
      ratingHotelService:
        max-attempts: 3
        wait-duration: 3s
```

### Highlights

- **Circuit Breaker**
  - Trips after 5 calls with ≥50% failure rate.
  - Waits 5 seconds before allowing trial calls in half-open state.
  - Health is exposed via Spring Boot Actuator.

- **Retry**
  - Retries failed service calls 3 times with 3-second intervals.
  - Fallback method is triggered on persistent failure.

![alt text](image.png)


### Rate Limiting with Resilience4j
Rate Limiter using Resilience4j's *@RateLimiter* annotation to ensure controlled access to the *getSingleUser* endpoint. This helps protect the service from overuse and potential @@denial-of-service (DoS) attacks.

###  `application.yml` Configuration
  ```yml
  ratelimiter:
  instances:
    userRateLimiter:
      limit-refresh-period: 4s       # Time window to reset the limit
      limit-for-period: 2            # Maximum number of calls in the above period
      timeout-duration: 0s           # Wait time to acquire permission if limit is reached
  ```
 ### Implementation Example
  ```java
  @GetMapping("/{userId}")
  @RateLimiter(name = "userRateLimiter", fallbackMethod = "ratingHotelFallBack")
  public ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId) {
      logger.info("Retry count : {}", retyCount);
      retyCount++;

      User user = userService.getUser(userId);
      return ResponseEntity.ok(user);
  }

      // Fallback Method
      // Write here
  ```
###  Test @RateLimiter Using JMeter

1. **Open JMeter**.

2. **Create a Test Plan**:
   - Right-click on **Test Plan** → `Add` → **Threads (Users)** → **Thread Group**.

3. **Configure Thread Group**:
   - Number of Threads (users): `5`
   - Ramp-Up Period: `1`
   - Loop Count: `5`

4. **Add an HTTP Request**:
   - Right-click on **Thread Group** → `Add` → **Sampler** → **HTTP Request**
   - Set the following:
     - **Method**: `GET`
     - **Server Name**: `localhost`
     - **Port Number**: `8080`
     - **Path**: `/users/123` (replace with a valid user ID)

5. **Add a Listener** to view results:
   - Right-click on **Thread Group** → `Add` → **Listener** → **View Results Tree** or **Summary Report**

6. **Run the Test**.
---


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

