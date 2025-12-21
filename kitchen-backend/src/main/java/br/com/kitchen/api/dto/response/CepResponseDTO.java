package br.com.kitchen.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CepResponseDTO {

    private String cep;
    @JsonProperty("street")
    private String logradouro;

    @JsonProperty("neighborhood")
    private String bairro;

    @JsonProperty("city")
    private String cidade;

    @JsonProperty("state")
    private String uf;
//    private String pais;

}