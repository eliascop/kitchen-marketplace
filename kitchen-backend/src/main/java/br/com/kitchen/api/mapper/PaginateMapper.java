package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.response.PaginatedResponse;
import org.springframework.data.domain.Page;

public class PaginateMapper {
    public static <T> PaginatedResponse<T> toDTO(Page<T> page) {
        return new PaginatedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
