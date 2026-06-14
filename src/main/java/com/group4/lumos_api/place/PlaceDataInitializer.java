package com.group4.lumos_api.place;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.group4.lumos_api.place.entity.Place;
import com.group4.lumos_api.place.repository.PlaceRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PlaceDataInitializer implements CommandLineRunner {

    private final PlaceRepository placeRepository;

    @Override
    public void run(String... args) {

        if (placeRepository.count() > 0) {
            return;
        }

        Place place = new Place();

        place.setName("블루포트 공학관점");
        place.setBuilding("공학관,공대");
        place.setType("카페");
        place.setTime("09:00 - 19:00");
        place.setLat(35.85910891900613);
        place.setLng(128.48747324155707);

        placeRepository.save(place);
    }
}