package com.jp.parkapi.services;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.repositories.ClientSpaceRepository;
import com.jp.parkapi.repositories.projection.ClientSpaceProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientSpaceService {

    @Autowired
    private ClientSpaceRepository clientSpaceRepository;

    @Transactional
    public ClientSpace insertClientSpace(ClientSpace clientSpace) {
        try {
            return clientSpaceRepository.save(clientSpace);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public ClientSpace findCheckInByReceiptNumberAndExitDateIsNull(String receipt) {
        return clientSpaceRepository.findByReceiptAndExitDateIsNull(receipt).orElseThrow(
                () -> new EntityNotFoundException(String.format("Check-In não encontrado ou Check-out já realizado com o recibo '%s' informado", receipt)));
    }

    @Transactional(readOnly = true)
    public long getTotalTimesParkingComplete(String cpf) {
        return clientSpaceRepository.countByClientCpfAndExitDateIsNotNull(cpf);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpaceProjection> findCheckInByClientCpf(String cpf, Pageable pageable) {
        return clientSpaceRepository.findByClientCpf(cpf, pageable).orElseThrow(
                () -> new EntityNotFoundException(String.format("Check-In não encontrado com o CPF '%s' informado", cpf)));
    }

    @Transactional(readOnly = true)
    public Page<ClientSpaceProjection> findByClientId(Long id, Pageable pageable) {
        return clientSpaceRepository.findByClientUserId(id, pageable).orElseThrow(
                () -> new EntityNotFoundException(String.format("Check-In não encontrado com o ID '%s' informado", id)));
    }
}
