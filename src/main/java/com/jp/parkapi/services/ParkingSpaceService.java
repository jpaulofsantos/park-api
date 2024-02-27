package com.jp.parkapi.services;

import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.exception.CodeUniqueViolationException;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.repositories.ParkingSpaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ParkingSpaceService {

    @Autowired
    private ParkingSpaceRepository parkingSpaceRepository;

    @Transactional
    public ParkingSpace insertSpace(ParkingSpace parkingSpace) {
        try {
            return parkingSpaceRepository.save(parkingSpace);
        } catch (DataIntegrityViolationException e) {
            throw new CodeUniqueViolationException(String.format("Vaga com o código '%s' já cadastrada", parkingSpace.getCode()));
        }
    }

    @Transactional(readOnly = true)
    public ParkingSpace findByCode(String code) {
        return parkingSpaceRepository.findByCode(code).orElseThrow(
                () -> new EntityNotFoundException(String.format("Vaga com o código '%s' não encontrada", code))
        );
    }
}
