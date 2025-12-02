package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.model.Catalog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CatalogRepository extends GenericRepository<Catalog, Long> {
    Optional<Catalog> findByNameAndSellerId(String name, Long sellerId);

    @Query("select c.slug from Catalog c where c.id = :id")
    String findSlugById(@Param("id") Long id);

    List<Catalog> findBySlug(String slug);

    @Query("select c from Catalog c where c.id in (select min(c2.id) from Catalog c2 group by c2.slug) order by c.name")
    List<Catalog> findAllDistinctBySlug();
}
