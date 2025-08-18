package ludo.mentis.aciem.ecm.rest.filter;

import jakarta.servlet.ServletException;
import ludo.mentis.aciem.ecm.domain.ApiKey;
import ludo.mentis.aciem.ecm.domain.CipherEnvelopeEntity;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.service.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class ApiKeyAuthFilterTest {

    private ApiKeyRepository apiKeyRepository;
    private PasswordService passwordService;
    private ApiKeyAuthFilter filter;

    @BeforeEach
    void setup() {
        apiKeyRepository = Mockito.mock(ApiKeyRepository.class);
        passwordService = Mockito.mock(PasswordService.class);
        filter = new ApiKeyAuthFilter(apiKeyRepository, passwordService);
    }

    @Test
    void shouldNotFilter_nonApiPaths() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/home");
        assertThat(filter.shouldNotFilter(request)).isTrue();
    }

    @Test
    void missingHeaders_shouldReturn401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/anything");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.getContentAsString()).contains("Client ID and API key are required");
    }

    @Test
    void clientIdNotFound_shouldReturn401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "abc");
        request.addHeader("X-API-CLIENT-ID", "client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.empty());

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Invalid client ID or API key.");
    }

    @Test
    void nullCipherEnvelope_shouldReturn401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "abc");
        request.addHeader("X-API-CLIENT-ID", "client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiKey key = new ApiKey();
        key.setCipherEnvelope(null);
        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.of(key));

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Invalid client ID or API key.");
    }

    @Test
    void decryptionError_shouldReturn500() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "abc");
        request.addHeader("X-API-CLIENT-ID", "client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiKey key = new ApiKey();
        key.setCipherEnvelope(new CipherEnvelopeEntity());
        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.of(key));
        when(passwordService.decryptPasswordFromEntity(key.getCipherEnvelope())).thenThrow(new RuntimeException("dec error"));

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getContentAsString()).contains("Internal server error while validating API key.");
    }

    @Test
    void mismatchedSecret_shouldReturn401() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "provided");
        request.addHeader("X-API-CLIENT-ID", "client");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiKey key = new ApiKey();
        key.setCipherEnvelope(new CipherEnvelopeEntity());
        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.of(key));
        when(passwordService.decryptPasswordFromEntity(key.getCipherEnvelope())).thenReturn("actual");

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("Invalid client ID or API key.");
    }

    @Test
    void serverHostMismatch_shouldReturn403() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "secret");
        request.addHeader("X-API-CLIENT-ID", "client");
        request.setRemoteHost("other.host");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiKey key = new ApiKey();
        key.setCipherEnvelope(new CipherEnvelopeEntity());
        key.setServer("allowed.host");
        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.of(key));
        when(passwordService.decryptPasswordFromEntity(key.getCipherEnvelope())).thenReturn("secret");

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("API key is not allowed from this host.");
    }

    @Test
    void success_shouldProceedDownChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/secure");
        request.addHeader("X-API-KEY", "secret");
        request.addHeader("X-API-CLIENT-ID", "client");
        request.setRemoteHost("client.host");
        MockHttpServletResponse response = new MockHttpServletResponse();

        ApiKey key = new ApiKey();
        key.setCipherEnvelope(new CipherEnvelopeEntity());
        key.setServer("CLIENT.HOST"); // case-insensitive match
        when(apiKeyRepository.findByClientId("client")).thenReturn(Optional.of(key));
        when(passwordService.decryptPasswordFromEntity(key.getCipherEnvelope())).thenReturn("secret");

        MockFilterChain chain = new MockFilterChain();
        filter.doFilter(request, response, chain);

        // Should be allowed through; default status remains 200 and body empty
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).isEmpty();
        // Also assert repository was queried with the provided client id
        Mockito.verify(apiKeyRepository).findByClientId(anyString());
    }
}
