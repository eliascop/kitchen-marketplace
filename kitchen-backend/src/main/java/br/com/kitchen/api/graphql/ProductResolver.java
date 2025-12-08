package br.com.kitchen.api.graphql;

import br.com.kitchen.api.dto.ProductDTO;
import br.com.kitchen.api.dto.response.PaginatedResponse;
import br.com.kitchen.api.model.Product;
import br.com.kitchen.api.service.ProductService;
import br.com.kitchen.api.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Duration;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductResolver {

    private final ProductService productService;
    private final RedisCacheService redisCacheService;

    @QueryMapping
    public Product product(@Argument Long id) {
        return productService.findProductById(id);
    }

    @QueryMapping
    public List<Product> products(@Argument int limit, @Argument int offset) {
        var page = PageRequest.of(offset / limit, limit);
        return productService.findActiveProducts(page).getContent();
    }

    @QueryMapping
    public PaginatedResponse<ProductDTO> searchProducts(@Argument String text,
                                                        @Argument int page,
                                                        @Argument int size) {

        final String key = "search:" + text.toLowerCase()
                + ":page:" + page
                + ":size:" + size;

        PaginatedResponse<ProductDTO> cached = redisCacheService.get(key);

        if (cached != null) return cached;

        var pageable = PageRequest.of(page, size);
        var result = productService.searchProducts(text, pageable);

        PaginatedResponse<ProductDTO> response = new PaginatedResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );

        redisCacheService.set(key, response, Duration.ofSeconds(45));

        return response;
    }
}