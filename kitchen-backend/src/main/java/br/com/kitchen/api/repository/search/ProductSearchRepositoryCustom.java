package br.com.kitchen.api.repository.search;

import br.com.kitchen.api.dto.search.ProductSearchDocumentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSearchRepositoryCustom {

    Page<ProductSearchDocumentDTO> search(String query, Pageable pageable);

}
