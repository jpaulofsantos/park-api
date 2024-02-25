package com.jp.parkapi.services;

import com.jp.parkapi.entities.Client;
import com.jp.parkapi.exception.CpfUniqueViolationException;
import com.jp.parkapi.exception.EntityNotFoundException;
import com.jp.parkapi.repositories.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Transactional
    public Client insertClient(Client client) {
        try {
            return clientRepository.save(client);
        } catch (DataIntegrityViolationException e) {
            throw new CpfUniqueViolationException(String.format("Usuário '%s' já cadastrado.", client.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Cliente '%s' não encontrado", id)));
    }
}
