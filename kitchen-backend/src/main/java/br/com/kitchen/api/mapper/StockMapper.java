package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.dto.StockHistoryDTO;
import br.com.kitchen.api.dto.response.StockResponseDTO;
import br.com.kitchen.api.model.Stock;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StockMapper {
    public static StockDTO toDTO(Stock stock) {
        if (stock == null) return null;

        return StockDTO.builder()
                .id(stock.getId())
                .sku(stock.getSku().getSku())
                .sellerId(stock.getSeller().getId())
                .stockAction(stock.getStockAction())
                .soldQuantity(stock.getSoldQuantity())
                .totalQuantity(stock.getTotalQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .build();
    }

    public static StockResponseDTO toStockResponseDTO(Stock stock) {
        if (stock == null) return null;
        return StockResponseDTO.builder()
                .totalQuantity(stock.getTotalQuantity())
                .reservedQuantity(stock.getReservedQuantity())
                .soldQuantity(stock.getSoldQuantity())
                .build();
    }

    public static StockHistoryDTO convertCreatedAt(StockHistoryDTO original) {
        return StockHistoryDTO.builder()
                .id(original.getId())
                .sku(original.getSku())
                .sellerId(original.getSellerId())
                .soldQuantity(original.getSoldQuantity())
                .reservedQuantity(original.getReservedQuantity())
                .totalQuantity(original.getTotalQuantity())
                .createdAt(formatCreatedAt(original.getCreatedAt()))
                .eventType(original.getEventType())
                .build();
    }

    private static String formatCreatedAt(String epochMillis) {
        return Instant.ofEpochMilli(Long.parseLong(epochMillis))
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}