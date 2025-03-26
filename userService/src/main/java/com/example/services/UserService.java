package com.example.services;

import com.example.entities.User;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {


    //create user
    User saveUser(User user);

    //get all user
    List<User> getAllUser();

    //get single user
    User getUser(String userId);

}
