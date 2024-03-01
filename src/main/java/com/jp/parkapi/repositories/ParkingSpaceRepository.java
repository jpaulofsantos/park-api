package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, Long> {

    Optional<ParkingSpace> findByCode(String code);


    Optional<ParkingSpace> findFirstByStatus(ParkingSpace.StatusParkingSpace statusParkingSpace);
}
