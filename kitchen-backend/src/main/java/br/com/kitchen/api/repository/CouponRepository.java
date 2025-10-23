package br.com.kitchen.api.repository;

import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponVisibility;
import br.com.kitchen.api.model.Coupon;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends GenericRepository<Coupon, String>{
    Optional<Coupon> findByCode(String code);

    Optional<Coupon> findByCodeAndActiveTrue(String code);

    List<Coupon> findByActiveTrueAndVisibilityAndScopeAndExpiresAtAfter(
            CouponVisibility visibility, CouponScope scope, LocalDateTime now
    );

    List<Coupon> findByActiveTrueAndScopeAndSellerIdAndExpiresAtAfter(
            CouponScope scope, Long sellerId, LocalDateTime now
    );

}
