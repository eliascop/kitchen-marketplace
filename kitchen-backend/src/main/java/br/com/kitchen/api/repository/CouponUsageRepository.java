package br.com.kitchen.api.repository;

import br.com.kitchen.api.model.CouponUsage;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponUsageRepository extends GenericRepository<CouponUsage, Long>{
    boolean existsByCouponIdAndBuyerId(String couponId, Long buyerId);
    long countByCouponId(String couponId);
    long countByCouponIdAndBuyerId(String couponId, Long buyerId);
}
