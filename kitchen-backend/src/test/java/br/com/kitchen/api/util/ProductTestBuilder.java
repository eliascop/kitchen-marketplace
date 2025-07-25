package br.com.kitchen.api.util;

import br.com.kitchen.api.model.Catalog;
import br.com.kitchen.api.model.Category;
import br.com.kitchen.api.model.Product;

import java.math.BigDecimal;

public class ProductTestBuilder {

    public static Product buildDefaultProduct() {
        return Product.builder()
                .name("Pizza")
                .description("Delicious thin crust pizza")
                .price(BigDecimal.valueOf(100))
                .catalog(Catalog.builder().name("Fast and food").build())
                .category(Category.builder().name("Meal").build())
                .build();
    }

    public static Product buildWithCustomValues(String name, BigDecimal price) {
        return Product.builder()
                .name(name)
                .description("Description for "+ name)
                .price(price)
                .catalog(Catalog.builder().name("Catalog of "+name).build())
                .category(Category.builder().name("Category of "+name).build())
                .build();
    }
}
