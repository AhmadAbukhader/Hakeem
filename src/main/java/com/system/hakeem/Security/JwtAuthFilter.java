package com.system.hakeem.Security;

import com.system.hakeem.Service.UserManagement.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
// import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // private final HandlerExceptionResolver handlerExceptionResolver;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        if (StringUtils.hasText(requestHeader) && requestHeader.startsWith("Bearer ")) {
            // extract token from header
            token = requestHeader.substring(7);
            try {
                // save username
                email = jwtService.extractUsername(token);

            } catch (IllegalArgumentException e) {
                logger.debug("Illegal Argument while fetching the username !!");
            } catch (ExpiredJwtException e) {
                logger.debug("Given jwt token is expired !!");
            } catch (MalformedJwtException e) {
                logger.debug("Some changed has done in token !! Invalid Token");
            }
        } else {
            logger.debug("Invalid Header Value - No Authorization header (expected for public endpoints)");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            Boolean validToken = this.jwtService.isTokenValid(token, userDetails);

            if (validToken) {
                // set the authentication
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } else {
                logger.debug("Validation failed ");
            }
        }
        filterChain.doFilter(request, response);
    }
}
