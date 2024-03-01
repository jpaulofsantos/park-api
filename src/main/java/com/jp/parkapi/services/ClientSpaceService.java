package com.jp.parkapi.services;

import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.repositories.ClientSpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
}
