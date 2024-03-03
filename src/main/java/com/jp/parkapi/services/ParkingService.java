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
        Client client = clientService.findByCpf(clientSpace.getClient().getCpf()); //pegando as infos do cliente pelo cpf recebido no clientSpace vindo do Controller
        clientSpace.setClient(client); //após a busca pelo cliente, substituimos este cliente que tinha somente o cpf (vindo do DTO), por um cliente completo, com as infos do BD

        ParkingSpace parkingSpace = parkingSpaceService.findByFreeParkingSpace(); //busca vaga livre
        parkingSpace.setStatusSpace(ParkingSpace.StatusParkingSpace.OCCUPIED); //seta a vaga como ocupada

        clientSpace.setParkingSpace(parkingSpace); //seta a vaga
        clientSpace.setEntryDate(LocalDateTime.now());
        clientSpace.setReceipt(ParkingUtils.createReceipt());

        return clientSpaceService.insertClientSpace(clientSpace);
    }
}
