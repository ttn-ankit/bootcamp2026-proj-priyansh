package com.example.ecommerceproject.filter;

import static lombok.AccessLevel.PRIVATE;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.ecommerceproject.service.UserSessionService;
import com.example.ecommerceproject.service.impl.CustomUserDetailsServiceImpl;
import com.example.ecommerceproject.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    final JwtUtil jwtUtil;
    final CustomUserDetailsServiceImpl userDetailsService;
    final UserSessionService userSessionService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            
            try {
                // 1. First validate JWT token (including expiry)
                if (!jwtUtil.isTokenValid(token)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // 2. Then check if token exists in DB (not revoked)
                String jti = jwtUtil.extractJti(token);
                if (!userSessionService.isAccessTokenValid(jti)) {
                    filterChain.doFilter(request, response);
                    return;
                }
                
                // 3. Extract email for user loading
                email = jwtUtil.extractEmail(token);
                
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // JWT validation already done above, just set authentication
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request));

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);
            } catch (Exception e) {
                
            }
        }

        filterChain.doFilter(request, response);
    }
}