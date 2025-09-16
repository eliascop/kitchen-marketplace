package br.com.kitchen.history.mapper;

import br.com.kitchen.history.dto.StockHistoryDTO;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StockHistoryMapper {

    public static StockHistoryDTO toDTO(Map<String, AttributeValue> item) {
        return StockHistoryDTO.builder()
                .id(Long.valueOf(item.get("id").n()))
                .sku(item.get("sku").s())
                .sellerId(Long.valueOf(item.get("sellerId").s()))
                .soldQuantity(Integer.valueOf(item.get("soldQuantity").s()))
                .reservedQuantity(Integer.valueOf(item.get("reservedQuantity").s()))
                .totalQuantity(Integer.valueOf(item.get("totalQuantity").s()))
                .createdAt(item.get("createdAt").s())
                .eventType(item.getOrDefault(
                        "stockAction",
                        AttributeValue.builder().s("UNKNOWN").build()
                ).s())
                .build();
    }

    public static List<StockHistoryDTO> toDTOList(List<Map<String, AttributeValue>> items) {
        return items.stream()
                .map(StockHistoryMapper::toDTO)
                .collect(Collectors.toList());
    }
}
