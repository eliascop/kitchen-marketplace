package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.StockDTO;
import br.com.kitchen.api.model.Stock;

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
}