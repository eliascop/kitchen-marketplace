package br.com.kitchen.api.client;

import br.com.kitchen.api.dto.response.CepResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CepProviderClient {

    @Value("${cep.provider.endpoint}")
    private String providerUrl;

    private final RestTemplate restTemplate;

    public CepProviderClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CepResponseDTO getCep(String cep) {
        return restTemplate.getForObject(providerUrl, CepResponseDTO.class, cep);
    }
}