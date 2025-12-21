package br.com.kitchen.api.service;

import br.com.kitchen.api.client.CepProviderClient;
import br.com.kitchen.api.dto.response.CepResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class CepService {

    private final CepProviderClient viaCepClient;

    public CepService(CepProviderClient viaCepClient) {
        this.viaCepClient = viaCepClient;
    }

    public CepResponseDTO findByCep(String cep) {
        return viaCepClient.getCep(cep);
    }
}
