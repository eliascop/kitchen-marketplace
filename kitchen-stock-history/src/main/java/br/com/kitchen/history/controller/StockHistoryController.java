package br.com.kitchen.history.controller;

import br.com.kitchen.history.service.StockHistoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/history")
public class StockHistoryController {

    private final StockHistoryService stockHistoryService;

    public StockHistoryController(StockHistoryService stockHistoryService) {
        this.stockHistoryService = stockHistoryService;
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<?> getStockHistories(@PathVariable Long sellerId) {
        try {
            return ResponseEntity.ok(stockHistoryService.getBySellerId(sellerId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "An error occurred on get stock histories:",
                    "details", e.getMessage())
            );
        }
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<?> getStockHistoriesBySku(@PathVariable String sku) {
        try {
            return ResponseEntity.ok(stockHistoryService.getBySku(sku));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "An error occurred on get stock history by sku",
                    "details", e.getMessage())
            );
        }
    }
}
