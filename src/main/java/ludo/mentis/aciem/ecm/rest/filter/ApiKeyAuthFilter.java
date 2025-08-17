package ludo.mentis.aciem.ecm.rest.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ApiKeyAuthFilter.class);
    private static final String API_PREFIX = "/api/**";
    private static final String HEADER_API_KEY = "X-API-KEY";
    private static final String HEADER_CLIENT_ID = "X-API-CLIENT-ID";

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordService passwordService;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public ApiKeyAuthFilter(ApiKeyRepository apiKeyRepository, PasswordService passwordService) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordService = passwordService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Only filter REST API calls under /api/**
        return !matcher.match(API_PREFIX, path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String providedKey = extractApiKey(request);
        String clientId = extractClientId(request);
        if (!StringUtils.hasText(providedKey) || !StringUtils.hasText(clientId)) {
            reject(response, HttpStatus.UNAUTHORIZED, "Client ID and API key are required. Provide them via '" + HEADER_CLIENT_ID + "' and '" + HEADER_API_KEY + "' headers or 'clientId' and 'apiKey' query parameters.");
            return;
        }

        String callerHost = safeRemoteHost(request);

        try {
            Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByClientId(clientId);
            if (apiKeyOpt.isEmpty()) {
                reject(response, HttpStatus.UNAUTHORIZED, "Invalid client ID or API key.");
                return;
            }
            ApiKey key = apiKeyOpt.get();
            if (key.getCipherEnvelope() == null) {
                reject(response, HttpStatus.UNAUTHORIZED, "Invalid client ID or API key.");
                return;
            }
            String secret;
            try {
                secret = passwordService.decryptPasswordFromEntity(key.getCipherEnvelope());
            } catch (Exception e) {
                log.warn("Failed to decrypt API key id={}", key.getId(), e);
                reject(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error while validating API key.");
                return;
            }
            if (!providedKey.equals(secret)) {
                reject(response, HttpStatus.UNAUTHORIZED, "Invalid client ID or API key.");
                return;
            }
            // API key matched; now check server restriction if any
            String server = key.getServer();
            if (StringUtils.hasText(server) && !server.equalsIgnoreCase(callerHost)) {
                reject(response, HttpStatus.FORBIDDEN, "API key is not allowed from this host.");
                return;
            }

            // Authorized
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            log.error("Error while validating API key", ex);
            reject(response, HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error while validating API key.");
        }
    }

    private String extractApiKey(HttpServletRequest request) {
        String header = request.getHeader(HEADER_API_KEY);
        if (StringUtils.hasText(header)) {
            return header.trim();
        }
        String query = request.getParameter("apiKey");
        return StringUtils.hasText(query) ? query.trim() : null;
    }

    private String extractClientId(HttpServletRequest request) {
        String header = request.getHeader(HEADER_CLIENT_ID);
        if (StringUtils.hasText(header)) {
            return header.trim();
        }
        String query = request.getParameter("clientId");
        return StringUtils.hasText(query) ? query.trim() : null;
    }

    private String safeRemoteHost(HttpServletRequest request) {
        try {
            String host = request.getRemoteHost();
            return host == null ? "" : host;
        } catch (Exception e) {
            return "";
        }
    }

    private void reject(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        String body = "{\"status\":" + status.value() + ",\"error\":\"" + status.getReasonPhrase() + "\",\"message\":\"" + escapeJson(message) + "\"}";
        response.getWriter().write(body);
    }

    private String escapeJson(String s) {
        return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
