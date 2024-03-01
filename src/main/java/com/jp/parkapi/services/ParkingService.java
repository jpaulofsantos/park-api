package com.jp.parkapi.services;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.util.ParkingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class ParkingService {

    @Autowired
    private ClientSpaceService clientSpaceService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @Transactional
    public ClientSpace checkIn(ClientSpace clientSpace) {
        Client client = clientService.findByCpf(clientSpace.getClient().getCpf());
        clientSpace.setClient(client); //substituindo o cliente que tinha somente o cpf, por um cliente completo

        ParkingSpace parkingSpace = parkingSpaceService.findByFreeParkingSpace();
        parkingSpace.setStatusSpace(ParkingSpace.StatusParkingSpace.OCCUPIED);

        clientSpace.setParkingSpace(parkingSpace);
        clientSpace.setEntryDate(LocalDateTime.now());
        clientSpace.setReceipt(ParkingUtils.createReceipt());

        return clientSpaceService.insertClientSpace(clientSpace);
    }
}
