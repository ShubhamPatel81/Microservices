package com.hotelService.services.impl;

import com.hotelService.entities.Hotel;
import com.hotelService.exceptions.ResourceNotFoundException;
import com.hotelService.repository.HotelRepo;
import com.hotelService.services.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HotelServiceImpl implements HotelService {

    @Autowired
    private HotelRepo hotelRepo;


    @Override
    public Hotel create(Hotel hotel) {
        String id=UUID.randomUUID().toString();
        hotel.setId(id);
        return hotelRepo.save(hotel);
    }

    @Override
    public List<Hotel> getAll() {
        return hotelRepo.findAll();
    }

    @Override
    public Hotel getSingleHote(String id) {
        return hotelRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel with given id not found!!!"));
    }
}
