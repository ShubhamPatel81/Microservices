package com.example.externalFienClient.Service;

import com.example.entities.Rating;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.Map;
@Service
@FeignClient("RATINGSERVICE")
public interface RatingService {


    // get


    //post rating

    @PostMapping("/ratings")
    public Rating  createRating( Rating values);

    // put rating

    @PutMapping("/ratings/{ratingId}")
    public Rating updateRating(@PathVariable("ratingId")String ratingId, Rating rating);

    //delete
    @DeleteMapping("/ratings/{ratingId}")
    public void deleteRating(@PathVariable String ratingId);
}
