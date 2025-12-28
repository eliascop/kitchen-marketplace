package br.com.kitchen.notification.security;

import br.com.kitchen.notification.utils.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Value("${app.internal-api-token}")
    private String internalToken;
    private final JwtTokenUtil jwtTokenUtil;

    public JwtAuthFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
     }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        final String requestTokenFromHeader =  request.getHeader("Authorization");

        if(requestTokenFromHeader == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if(requestTokenFromHeader.equals(internalToken)){
            filterChain.doFilter(request, response);
            return;
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null && requestTokenFromHeader.startsWith("Bearer ")) {
            String jwtToken = requestTokenFromHeader.substring(7);

            Long userId = jwtTokenUtil.getUserIdFromToken(jwtToken);
            boolean isSeller = jwtTokenUtil.IsSellerFromToken(jwtToken);
            boolean isCustomer = jwtTokenUtil.IsCustomerFromToken(jwtToken);

            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            UserPrincipal userPrincipal = new UserPrincipal(userId, isSeller, isCustomer);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    Collections.emptyList()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
