package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.response.CepResponseDTO;
import br.com.kitchen.api.service.CepService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/cep/v1")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @GetMapping("/{cep}")
    public ResponseEntity<?> getCep(@PathVariable String cep) {
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(cepService.findByCep(cep));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "code", HttpStatus.BAD_REQUEST.value(),
                    "message", "An error has occurred when getCep:",
                    "details", e.getMessage()
            ));
        }
    }
}
