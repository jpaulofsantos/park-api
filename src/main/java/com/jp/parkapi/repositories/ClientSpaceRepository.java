package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.entities.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientSpaceRepository extends JpaRepository<ClientSpace, Long> {

    //@Query("SELECT space.receipt from ClientSpace space WHERE space.receipt LIKE :receipt AND space.exitDate is NULL")
    Optional<ClientSpace> findByReceiptAndExitDateIsNull(String receipt);

    long countByClientCpfAndExitDateIsNotNull(String cpf);
}
