package com.example.controller;

import com.example.entities.User;
import com.example.services.UserService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
//import lombok.Builder;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@Builder
@RestController
@RequestMapping("/users")
public class UserController {

    private Logger logger = LoggerFactory.getLogger(LoggerFactory.class);
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user ){
       User user1= userService.saveUser(user);
       return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    int retyCount = 1;


    //get single user
    @GetMapping("/{userId}")
//    @CircuitBreaker(name = "ratingHotelBreaker", fallbackMethod = "ratingHotelFallBack")
    @Retry(name ="ratingHotelService", fallbackMethod ="ratingHotelFallBack" )
    public  ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId){

        //checking retry method

        logger.info("Retry count : {}",retyCount);
        retyCount++;

      User user =  userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    //creating fall back method for circuitBreaker
    //fall back return type and the main method where fall back is user both return type should be same



    public ResponseEntity<User> ratingHotelFallBack(String userId, Exception ex){
        System.out.println("FallBack is executed becuase service is down !!!"+ ex.getMessage());


        User user = User.builder()
                .email("dummy@gmail.com")
                .name("dummy")
                .about("This user is created because some service is down !!!")
                .userId("121")
                .build();

        return new ResponseEntity<>(user, HttpStatus.OK);
    }




    @GetMapping
    public ResponseEntity<List<User>> getAllUser() {
        List<User> allUsers = userService.getAllUser();

        if (allUsers.isEmpty()) {
            return ResponseEntity.noContent().build(); // Returns 204 No Content if the list is empty
        }

        return ResponseEntity.ok(allUsers); // Returns 200 OK with the list
    }

}
