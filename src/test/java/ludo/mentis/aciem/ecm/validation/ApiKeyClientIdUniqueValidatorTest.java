package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ApiKeyClientIdUniqueValidatorTest {

    private ApiKeyService apiKeyService;
    private HttpServletRequest request;
    private ApiKeyClientIdUnique.ApiKeyClientIdUniqueValidator validator;

    @BeforeEach
    void setUp() {
        apiKeyService = mock(ApiKeyService.class);
        request = mock(HttpServletRequest.class);
        validator = new ApiKeyClientIdUnique.ApiKeyClientIdUniqueValidator(apiKeyService, request);
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_EditingAndClientIdUnchanged_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "9"));
        ApiKeyDTO existing = new ApiKeyDTO();
        existing.setId(9L);
        existing.setClientId("CLIENT-ABC");
        when(apiKeyService.get(9L)).thenReturn(existing);

        assertTrue(validator.isValid("client-abc", null), "Same clientId (case-insensitive) on edit should be valid");
        verify(apiKeyService, never()).clientIdExists(anyString());
    }

    @Test
    void isValid_ClientIdAlreadyExists_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(apiKeyService.clientIdExists("CID-1")).thenReturn(true);

        assertFalse(validator.isValid("CID-1", null));
    }

    @Test
    void isValid_ClientIdDoesNotExist_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(apiKeyService.clientIdExists("UNIQUE-CID")).thenReturn(false);

        assertTrue(validator.isValid("UNIQUE-CID", null));
    }

    @Test
    void isValid_InvalidIdFallsBackToExists_ChecksExistence() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "oops"));
        when(apiKeyService.clientIdExists("DUP-CID")).thenReturn(true);

        assertFalse(validator.isValid("DUP-CID", null));
        verify(apiKeyService).clientIdExists("DUP-CID");
    }
}
