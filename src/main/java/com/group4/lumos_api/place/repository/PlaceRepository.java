package com.group4.lumos_api.place.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group4.lumos_api.place.entity.Place;
public interface PlaceRepository extends JpaRepository<Place, Long> {
}