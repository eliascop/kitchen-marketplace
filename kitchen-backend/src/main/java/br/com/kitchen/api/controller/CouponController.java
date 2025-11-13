package br.com.kitchen.api.controller;

import br.com.kitchen.api.dto.CouponDTO;
import br.com.kitchen.api.dto.request.ApplyCouponRequestDTO;
import br.com.kitchen.api.dto.response.ApplyCouponResponseDTO;
import br.com.kitchen.api.enumerations.CouponScope;
import br.com.kitchen.api.enumerations.CouponVisibility;
import br.com.kitchen.api.mapper.PaginateMapper;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/coupons/v1")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    public ResponseEntity<?> createCoupon(@AuthenticationPrincipal UserPrincipal userDetails,
                                          @RequestBody CouponDTO couponDTO) {
        try {
            CouponDTO coupon = couponService.saveOrUpdate(userDetails.getSeller().get(), couponDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(coupon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on coupon creation",
                            "details", e.getMessage()
                    ));
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @PutMapping
    public ResponseEntity<?> updateCoupon(@AuthenticationPrincipal UserPrincipal userDetails,
                                          @RequestBody CouponDTO couponDTO) {
        try {
            CouponDTO coupon = couponService.saveOrUpdate(userDetails.getSeller().get(), couponDTO);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(coupon);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on coupon update",
                            "details", e.getMessage()
                    ));
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @GetMapping("/seller")
    public ResponseEntity<?> listSellerCoupons(@AuthenticationPrincipal UserPrincipal userDetails,
                                               @RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "20") int size){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(PaginateMapper.toDTO(couponService.findActiveCouponsBySeller(userDetails.getSeller().orElseThrow(), Pageable.ofSize(size).withPage(page))));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on list seller coupons",
                            "details", e.getMessage()
                    ));
        }
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/seller/{id}/deactivate")
    public ResponseEntity<?> deactivateCoupon(@AuthenticationPrincipal UserPrincipal userDetails,
                                              @PathVariable String id) {
        try {
            couponService.deactivateCoupon(id);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                    body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on deactivate coupon",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> listAvailableCoupons(@AuthenticationPrincipal UserPrincipal userDetails,
                                                  @RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(couponService.findAvailableCoupons(CouponVisibility.PUBLIC, CouponScope.GLOBAL, Pageable.ofSize(size).withPage(page)));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on list available coupons",
                            "details", e.getMessage()
                    ));
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<?> applyCoupon(@AuthenticationPrincipal UserPrincipal userDetails,
                                         @RequestBody ApplyCouponRequestDTO request) {
        try {
            ApplyCouponResponseDTO result = couponService.applyCoupon(request);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on apply coupon",
                            "details", e.getMessage()
                    ));
        }
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> getCouponByCode(@AuthenticationPrincipal UserPrincipal userDetails,
                                             @PathVariable String code) {
        try {
            CouponDTO coupon = couponService.findByCode(code);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(coupon);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "code", HttpStatus.BAD_REQUEST.value(),
                            "message", "An error has occurred on get coupon by code",
                            "details", e.getMessage()
                    ));
        }
    }
}
