package br.com.kitchen.api.repository.jpa;

import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponVisibility;
import br.com.kitchen.api.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends GenericRepository<Coupon, String>{
    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByCodeAndActiveTrue(String code);

    Page<Coupon> findByActiveTrueAndVisibilityAndScopeAndExpiresAtAfter(
            CouponVisibility visibility,
            CouponScope scope,
            LocalDateTime now,
            Pageable pageable
    );

    Page<Coupon> findByActiveTrueAndScopeAndSellerIdAndExpiresAtAfter(
            CouponScope scope,
            Long sellerId,
            LocalDateTime now,
            Pageable pageable
    );

}
