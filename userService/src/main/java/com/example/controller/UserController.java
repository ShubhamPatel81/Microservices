package com.example.controller;

import com.example.entities.User;
import com.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user ){
       User user1= userService.saveUser(user);
       return ResponseEntity.status(HttpStatus.CREATED).body(user1);
    }

    //get single user
    @GetMapping("/{userId}")
    public  ResponseEntity<User> getSingleUser(@PathVariable("userId") String userId){
      User user =  userService.getUser(userId);
        return ResponseEntity.ok(user);
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
