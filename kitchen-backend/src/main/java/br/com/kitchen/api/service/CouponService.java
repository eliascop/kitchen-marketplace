package br.com.kitchen.api.service;

import br.com.kitchen.api.dto.CouponDTO;
import br.com.kitchen.api.dto.request.ApplyCouponRequestDTO;
import br.com.kitchen.api.dto.response.ApplyCouponResponseDTO;
import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponType;
import br.com.kitchen.api.enumerations.CouponVisibility;
import br.com.kitchen.api.mapper.CouponMapper;
import br.com.kitchen.api.model.Coupon;
import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.repository.jpa.CouponRepository;
import br.com.kitchen.api.repository.jpa.CouponUsageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService extends GenericService<Coupon, String>{

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public CouponService(CouponRepository couponRepository,
                         CouponUsageRepository couponUsageRepository) {
        super(couponRepository, Coupon.class);
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
    }

    public CouponDTO saveOrUpdate(CouponDTO dto) {
        Coupon entity;

        if (dto.getId() != null && couponRepository.existsById(dto.getId())) {
            entity = couponRepository.findById(dto.getId())
                    .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));

            entity.setCode(dto.getCode());
            entity.setCouponType(dto.getCouponType());
            entity.setAmount(dto.getAmount());
            entity.setScope(dto.getScope());
            entity.setVisibility(dto.getVisibility());
            entity.setIssuerId(dto.getIssuerId());
            entity.setSellerId(dto.getSellerId());
            entity.setApplicableProductIds(dto.getApplicableProductIds());
            entity.setAllowedBuyerIds(dto.getAllowedBuyerIds());
            entity.setMinOrderAmount(dto.getMinOrderAmount());
            entity.setMaxDiscountAmount(dto.getMaxDiscountAmount());
            entity.setUsageLimitTotal(dto.getUsageLimitTotal());
            entity.setUsageLimitPerBuyer(dto.getUsageLimitPerBuyer());
            entity.setStartsAt(dto.getStartsAt());
            entity.setExpiresAt(dto.getExpiresAt());
            entity.setActive(dto.isActive());

        } else {
            entity = CouponMapper.toEntity(dto);
            entity.setUsageCountTotal(0);
            entity.setActive(true);
        }

        Coupon saved = couponRepository.save(entity);
        return CouponMapper.toDTO(saved);
    }

    public List<CouponDTO> findAvailableCoupons(CouponVisibility visibility, CouponScope scope) {
        return couponRepository
                .findByActiveTrueAndVisibilityAndScopeAndExpiresAtAfter(visibility, scope, LocalDateTime.now())
                .stream()
                .map(CouponMapper::toDTO)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<CouponDTO> findActivePublicCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository
                .findByActiveTrueAndVisibilityAndScopeAndExpiresAtAfter(
                        CouponVisibility.PUBLIC, CouponScope.GLOBAL, now);
        return CouponMapper.toDTOList(coupons);
    }

    @Transactional(readOnly = true)
    public List<CouponDTO> findActiveCouponsBySeller(Seller seller) {
        List<Coupon> coupons = couponRepository
                .findByActiveTrueAndScopeAndSellerIdAndExpiresAtAfter(
                        CouponScope.SELLER, seller.getId(), LocalDateTime.now());
        return CouponMapper.toDTOList(coupons);
    }

    @Transactional
    public void deactivateCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Transactional(readOnly = true)
    public CouponDTO findByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Coupon not found"));
        return CouponMapper.toDTO(coupon);
    }

    public ApplyCouponResponseDTO applyCoupon(ApplyCouponRequestDTO request) {
        Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                .filter(Coupon::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Coupon invalid"));

        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Coupon expired");
        }

        boolean alreadyUsed = couponUsageRepository.existsByCouponIdAndBuyerId(coupon.getId(), request.getBuyerId());
        if (alreadyUsed && coupon.getUsageLimitPerBuyer() <= 1) {
            throw new IllegalStateException("You already used this coupon");
        }

        long totalUsed = couponUsageRepository.countByCouponId(coupon.getId());
        if (coupon.getUsageLimitTotal() > 0 && totalUsed >= coupon.getUsageLimitTotal()) {
            throw new IllegalStateException("Total use limit was reached for this coupon");
        }

        long usedByBuyer = couponUsageRepository.countByCouponIdAndBuyerId(coupon.getId(), request.getBuyerId());
        if (coupon.getUsageLimitPerBuyer() > 0 && usedByBuyer >= coupon.getUsageLimitPerBuyer()) {
            throw new IllegalStateException("The limit by buyer has reached for this coupon");
        }

        if (coupon.getSellerId() != null &&
                request.getItems().stream().noneMatch(item -> item.getSkuId().equals(coupon.getSellerId()))) {
            throw new IllegalStateException("Coupon is not applicable to this seller.");
        }

        BigDecimal subtotal = calculateSubtotal(request);
        BigDecimal discount = getDiscount(coupon, subtotal);
        BigDecimal totalAfter = subtotal.subtract(discount);

        return ApplyCouponResponseDTO.builder()
                .couponCode(coupon.getCode())
                .applicable(true)
                .reason("Coupon applied with success.")
                .discount(discount)
                .subtotal(subtotal)
                .totalAfter(totalAfter)
                .build();
    }

    private static BigDecimal getDiscount(Coupon coupon, BigDecimal subtotal) {
        BigDecimal discount;
        if (coupon.getCouponType() == CouponType.PERCENTUAL) {
            discount = subtotal
                    .multiply(coupon.getAmount())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getAmount().setScale(2, RoundingMode.HALF_UP);
        }

        if (coupon.getMaxDiscountAmount() != null &&
                discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount;
    }

    private BigDecimal calculateSubtotal(ApplyCouponRequestDTO request) {
        return request.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
