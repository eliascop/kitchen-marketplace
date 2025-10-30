package br.com.kitchen.api.dto.search;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuAttributeSearchDocumentDTO {

    private String name;
    private String value;
}