package com.ratingService.service;

import com.ratingService.entities.Rating;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RatingService
{

    //create
    Rating createRating(Rating rating);



    //getAll Rating
    List<Rating> getAllRating();


    // getALL rating by user id
    List<Rating> getRatingByUserId(String userId);


    // get all rating by hotel id
    List<Rating> getRatingByHoteId(String hotelId);
}
