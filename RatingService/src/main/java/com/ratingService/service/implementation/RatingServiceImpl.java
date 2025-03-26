package com.ratingService.service.implementation;

import com.ratingService.entities.Rating;
import com.ratingService.repository.RatingRepository;
import com.ratingService.service.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RatingServiceImpl implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;


    @Override
    public Rating createRating(Rating rating) {
       String ratingId= UUID.randomUUID().toString();
       rating.setRatingId(ratingId);
        return ratingRepository.save(rating);
    }

    @Override
    public List<Rating> getAllRating() {
        return ratingRepository.findAll();
    }

    @Override
    public List<Rating> getRatingByUserId(String userId) {
        return ratingRepository.findByUserId(userId);
    }

    @Override
    public List<Rating> getRatingByHoteId(String hotelId) {
        return ratingRepository.findByHotelId(hotelId);
    }
}
