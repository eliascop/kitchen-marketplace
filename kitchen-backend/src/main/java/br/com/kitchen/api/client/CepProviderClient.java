package br.com.kitchen.api.client;

import br.com.kitchen.api.dto.response.CepResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ViaCepClient {

    @Value("${via.cep.endpoint}")
    private String viaCepUrl;
    private final RestTemplate restTemplate;

    public ViaCepClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CepResponseDTO getCep(String cep) {
        return restTemplate.getForObject(viaCepUrl, CepResponseDTO.class, cep);
    }
}