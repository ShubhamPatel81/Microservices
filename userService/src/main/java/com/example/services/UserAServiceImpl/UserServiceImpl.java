package com.example.services.UserAServiceImpl;

import com.example.entities.Hotel;
import com.example.entities.Rating;
import com.example.entities.User;
import com.example.externalFienClient.Service.HotelService;
import com.example.repository.UserReopsitory;
import com.example.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserReopsitory userReopsitory;


    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private HotelService hotelService;


    private Logger logger= LoggerFactory.getLogger(UserService.class);
    @Override
    public User saveUser(User user) {

        //generate unique user id
       String randonUserId= UUID.randomUUID().toString();
        user.setUserId(randonUserId);
        return userReopsitory.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userReopsitory.findAll();
    }

    @Override
    public User getUser(String userId) {

        // gt user by userId from user Repository
        User user = userReopsitory.findById(userId).orElseThrow(()->new ResourceAccessException("User not fount with the given ID "+userId));

        // fetch eating of the above user from RATING SERVICE
        //http://localhost:8083/ratings/user/ce6690a2-276a-48e3-b06b-30ca9b512754

      Rating[] ratingOfUser=  restTemplate.getForObject("http://RATING-SERVICE/ratings/user/"+user.getUserId(), Rating[].class);
        logger.info("{}",ratingOfUser);

       List<Rating> ratings = Arrays.stream(ratingOfUser).toList();

        List<Rating> ratingList = ratings.stream().map(rating -> {
           // call api to hotel service to get hotel and

            // return the rating


            //http://localhost:8082/hotels/b2e2166f-802e-4200-9129-cf7f041de7a
            //without using feign client
//          ResponseEntity<Hotel> forHotel= restTemplate.getForEntity("http://HOTEL-SERVICE/hotels/"+rating.getHotelId(), Hotel.class);
//            Hotel hotel = forHotel.getBody();

            // using feign client

            Hotel hotel = hotelService.getHotels(rating.getHotelId());

            // set the hotel to service
            rating.setHotel(hotel);

            return  rating;
        }) .collect(Collectors.toList());

        user.setRatings(ratingList);

        return user;
    }
}
