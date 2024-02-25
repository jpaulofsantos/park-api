package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

}
