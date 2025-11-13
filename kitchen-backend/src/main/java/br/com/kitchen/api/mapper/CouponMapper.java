package br.com.kitchen.api.mapper;

import br.com.kitchen.api.dto.CouponDTO;
import br.com.kitchen.api.dto.response.CouponResponseDTO;
import br.com.kitchen.api.model.Coupon;

import java.util.List;

public class CouponMapper {

    public static CouponDTO toDTO(Coupon coupon) {
        if (coupon == null) return null;

        return CouponDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .couponType(coupon.getCouponType())
                .amount(coupon.getAmount())
                .scope(coupon.getScope())
                .visibility(coupon.getVisibility())
                .issuerId(coupon.getIssuerId())
                .applicableProductIds(coupon.getApplicableProductIds())
                .allowedBuyerIds(coupon.getAllowedBuyerIds())
                .minOrderAmount(coupon.getMinOrderAmount())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .usageLimitTotal(coupon.getUsageLimitTotal())
                .usageLimitPerBuyer(coupon.getUsageLimitPerBuyer())
                .usageCountTotal(coupon.getUsageCountTotal())
                .createdAt(coupon.getCreatedAt())
                .startsAt(coupon.getStartsAt())
                .expiresAt(coupon.getExpiresAt())
                .updatedAt(coupon.getUpdatedAt())
                .active(coupon.isActive())
                .build();
    }

    public static Coupon toEntity(CouponDTO dto) {
        if (dto == null) return null;

        return Coupon.builder()
                .id(dto.getId())
                .code(dto.getCode())
                .couponType(dto.getCouponType())
                .amount(dto.getAmount())
                .scope(dto.getScope())
                .visibility(dto.getVisibility())
                .issuerId(dto.getIssuerId())
                .sellerId(dto.getSellerId())
                .applicableProductIds(dto.getApplicableProductIds())
                .allowedBuyerIds(dto.getAllowedBuyerIds())
                .minOrderAmount(dto.getMinOrderAmount())
                .maxDiscountAmount(dto.getMaxDiscountAmount())
                .usageLimitTotal(dto.getUsageLimitTotal())
                .usageLimitPerBuyer(dto.getUsageLimitPerBuyer())
                .usageCountTotal(dto.getUsageCountTotal())
                .startsAt(dto.getStartsAt())
                .expiresAt(dto.getExpiresAt())
                .build();
    }

    public static CouponResponseDTO toResponseDTO(Coupon coupon) {
        if (coupon == null) return null;

        return CouponResponseDTO.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .type(coupon.getCouponType())
                .amount(coupon.getAmount())
                .scope(coupon.getScope())
                .visibility(coupon.getVisibility())
                .sellerId(coupon.getSellerId())
                .active(coupon.isActive())
                .startsAt(coupon.getStartsAt())
                .expiresAt(coupon.getExpiresAt())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }

    public static List<CouponDTO> toDTOList(List<Coupon> coupons) {
        return coupons == null ? List.of() : coupons.stream()
                .map(CouponMapper::toDTO)
                .toList();
    }

    public static List<Coupon> toEntityList(List<CouponDTO> dtos) {
        return dtos == null ? List.of() : dtos.stream()
                .map(CouponMapper::toEntity)
                .toList();
    }
}
