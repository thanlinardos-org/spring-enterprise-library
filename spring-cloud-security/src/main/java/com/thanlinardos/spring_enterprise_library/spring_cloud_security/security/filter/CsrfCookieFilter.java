package com.thanlinardos.spring_enterprise_library.spring_cloud_security.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Filter that ensures the CSRF token is loaded and rendered to a cookie for each request.
 */
@Slf4j
public class CsrfCookieFilter extends OncePerRequestFilter {

    /**
     * Default constructor.
     */
    public CsrfCookieFilter() {
        // Default constructor
    }

    /**
     * Ensures the CSRF token is loaded and rendered to a cookie for each request.
     * Hooks into the servlet {@link FilterChain} to process the request and response.
     *
     * @param request     the HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet exception occurs
     * @throws IOException      if an I/O exception occurs
     */
    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        // Render the token value to a cookie by causing the deferred token to be loaded
        Optional.ofNullable(request.getAttribute(CsrfToken.class.getName()))
                .map(CsrfToken.class::cast)
                .ifPresentOrElse(CsrfToken::getToken,
                        () -> log.debug("No csrf token found for {}", request.getRequestURI()));
        filterChain.doFilter(request, response);
    }
}
