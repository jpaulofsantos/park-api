package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.repositories.projection.ClientProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query("SELECT client from Client client")
    Page<ClientProjection> findAllPageable(Pageable pageable);

}
