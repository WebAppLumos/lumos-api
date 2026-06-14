package com.group4.lumos_api.place.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group4.lumos_api.place.entity.Place;
import com.group4.lumos_api.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }
}