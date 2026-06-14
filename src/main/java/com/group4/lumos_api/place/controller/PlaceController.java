package com.group4.lumos_api.place.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group4.lumos_api.place.entity.Place;
import com.group4.lumos_api.place.service.PlaceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping
    public List<Place> getPlaces() {
        return placeService.getAllPlaces();
    }
}

