package br.com.kitchen.lambda.document;

import br.com.kitchen.lambda.dto.SellerDTO;

public record SellerIndexDocument(
        Long id,
        String storeName
) {
    public static SellerIndexDocument from(SellerDTO sellerDTO) {
        return new SellerIndexDocument(
                sellerDTO.getId(),
                sellerDTO.getStoreName()
        );
    }
}
