package br.com.kitchen.api.controller;

import br.com.kitchen.api.model.Wallet;
import br.com.kitchen.api.model.WalletTransaction;
import br.com.kitchen.api.dto.request.DebitRequest;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.service.WalletService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/wallets/v1")
@SecurityRequirement(name = "bearer-key")
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService){
        this.walletService = walletService;
    }

    @GetMapping
    public Wallet getWallet(@AuthenticationPrincipal UserPrincipal userDetails) {
        return walletService.getOrCreateWallet(userDetails.user().getId());
    }

    @PostMapping("/debit")
    public ResponseEntity<?> debit(@RequestBody DebitRequest debitRequest,
                                   @AuthenticationPrincipal UserPrincipal userDetails) {
        walletService.debit(userDetails.user().getId(), debitRequest.amount(), debitRequest.description());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<WalletTransaction>> getTransactions(@AuthenticationPrincipal UserPrincipal userDetails) {
        return walletService.getTransactions(userDetails.user().getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/balance")
    public ResponseEntity<BigDecimal> getBalance(@AuthenticationPrincipal UserPrincipal userDetails) {
        BigDecimal balance = walletService.getBalanceForUser(userDetails.user().getId());
        return ResponseEntity.ok(balance);
    }
}
