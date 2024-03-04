package com.jp.parkapi.services;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.entities.ClientSpace;
import com.jp.parkapi.entities.ParkingSpace;
import com.jp.parkapi.repositories.projection.ClientSpaceProjection;
import com.jp.parkapi.util.ParkingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public ClientSpace findCheckInByCode(String code) {
        return clientSpaceService.findCheckInByReceiptNumberAndExitDateIsNull(code);
    }

    @Transactional
    public ClientSpace checkOut(String receipt) {
        ClientSpace clientSpace = findCheckInByCode(receipt);
        LocalDateTime exitDate = LocalDateTime.now();

        BigDecimal value = ParkingUtils.calculateCost(clientSpace.getEntryDate(), exitDate);
        clientSpace.setValue(value);

        long totalTimes = clientSpaceService.getTotalTimesParkingComplete(clientSpace.getClient().getCpf());
        clientSpace.setDiscount(ParkingUtils.calculateDiscount(clientSpace.getValue(), totalTimes));

        clientSpace.setExitDate(exitDate);
        clientSpace.getParkingSpace().setStatusSpace(ParkingSpace.StatusParkingSpace.FREE);

        return clientSpaceService.insertClientSpace(clientSpace);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpaceProjection> findByClientCpf(String cpf, Pageable pageable) {
        return clientSpaceService.findCheckInByClientCpf(cpf, pageable);
    }

    @Transactional(readOnly = true)
    public Page<ClientSpaceProjection> findByByUserId(Long id, Pageable pageable) {
        return  clientSpaceService.findByClientId(id, pageable);
    }
}
