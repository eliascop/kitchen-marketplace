package br.com.kitchen.api.service;

import br.com.kitchen.api.model.Seller;
import br.com.kitchen.api.model.User;
import br.com.kitchen.api.security.UserPrincipal;
import br.com.kitchen.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + login));

        Seller seller = sellerService.findByUserId(user);

        return new UserPrincipal(user, seller);
    }
}
