package com.system.hakeem.Security;

import com.system.hakeem.Service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private HandlerExceptionResolver handlerExceptionResolver;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {


        String requestHeader = request.getHeader("Authorization");
        String email = null;
        String token = null;

        if(StringUtils.hasText(requestHeader) && requestHeader.startsWith("Bearer")) {
            //extract token from header
            token = requestHeader.substring(7);
            try{
                //save username
                email = this.jwtService.extractUsername(token);

            }catch (IllegalArgumentException e) {
                logger.info("Illegal Argument while fetching the username !!");
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired !!");
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token !! Invalid Token");
            }
        }else{logger.info("Invalid Header Value");}

        if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
            Boolean validToken = this.jwtService.isTokenValid(token, userDetails);

            if(validToken){
                //set the authentication
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails , null , userDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }else { logger.info("Validation failed "); }
        }
        filterChain.doFilter(request, response);
    }
}
