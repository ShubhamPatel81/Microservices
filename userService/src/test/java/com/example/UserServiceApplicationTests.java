package com.example;

import com.example.entities.Rating;
import com.example.entities.User;
import com.example.externalFienClient.Service.RatingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private RatingService ratingService;

	@Test
	void createRating(){


		Rating rating = Rating.builder()
				.rating(10)
				.userId("")
				.hotelId("")
				.feedback("This is create a test of feignClient")
				.rating(4)
				.build();

	Rating saveRating=ratingService.createRating(rating);
		System.out.println("saved rating "+ saveRating);

	}
}
