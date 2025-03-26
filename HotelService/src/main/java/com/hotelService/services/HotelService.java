package com.hotelService.services;

import com.hotelService.entities.Hotel;

import java.util.List;

public interface HotelService {
    //create
    Hotel create(Hotel hotel);

    //GET ALL
    List<Hotel> getAll();

    //get Single
    Hotel getSingleHote(String id);
}
