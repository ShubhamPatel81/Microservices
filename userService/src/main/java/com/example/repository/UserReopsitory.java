package com.example.repository;

import com.example.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserReopsitory extends JpaRepository<User, String> {


}
