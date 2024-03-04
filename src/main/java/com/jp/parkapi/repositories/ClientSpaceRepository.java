package com.jp.parkapi.repositories;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.repositories.projection.ClientSpaceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientSpaceRepository extends JpaRepository<ClientSpace, Long> {

    //@Query("SELECT space.receipt from ClientSpace space WHERE space.receipt LIKE :receipt AND space.exitDate is NULL")
    Optional<ClientSpace> findByReceiptAndExitDateIsNull(String receipt);

    long countByClientCpfAndExitDateIsNotNull(String cpf);

    Optional<Page<ClientSpaceProjection>> findByClientCpf(String cpf, Pageable pageable);

    Optional<Page<ClientSpaceProjection>> findByClientUserId(Long id, Pageable pageable);
}
