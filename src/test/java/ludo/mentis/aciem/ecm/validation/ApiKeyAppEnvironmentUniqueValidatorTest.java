package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.ApiKeyRepository;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ApiKeyAppEnvironmentUniqueValidatorTest {

    private ApiKeyService apiKeyService;
    private ApiKeyRepository apiKeyRepository;
    private HttpServletRequest request;
    private ApiKeyAppEnvironmentUnique.ApiKeyAppEnvironmentUniqueValidator validator;

    @BeforeEach
    void setUp() {
        apiKeyService = mock(ApiKeyService.class);
        apiKeyRepository = mock(ApiKeyRepository.class);
        request = mock(HttpServletRequest.class);
        validator = new ApiKeyAppEnvironmentUnique.ApiKeyAppEnvironmentUniqueValidator(apiKeyService, apiKeyRepository, request);
    }

    @Test
    void isValid_NullDto_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_MissingFields_ReturnsTrue() {
        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setApplicationId(10L);
        dto.setEnvironment(null);
        assertTrue(validator.isValid(dto, null));

        ApiKeyDTO dto2 = new ApiKeyDTO();
        dto2.setApplicationId(null);
        dto2.setEnvironment(Environment.DEV);
        assertTrue(validator.isValid(dto2, null));
    }

    @Test
    void isValid_EditAndPairUnchanged_ReturnsTrue() {
        ApiKeyDTO existing = new ApiKeyDTO();
        existing.setId(7L);
        existing.setApplicationId(11L);
        existing.setEnvironment(Environment.QA);

        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "7"));
        when(apiKeyService.get(7L)).thenReturn(existing);

        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setApplicationId(11L);
        dto.setEnvironment(Environment.QA);

        assertTrue(validator.isValid(dto, null));
        verify(apiKeyRepository, never()).existsByApplicationIdAndEnvironmentId(any(), any());
    }

    @Test
    void isValid_DuplicateExists_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setApplicationId(1L);
        dto.setEnvironment(Environment.UAT);

        when(apiKeyRepository.existsByApplicationIdAndEnvironmentId(1L, Environment.UAT.getId()))
                .thenReturn(true);

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void isValid_NotExisting_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        ApiKeyDTO dto = new ApiKeyDTO();
        dto.setApplicationId(2L);
        dto.setEnvironment(Environment.PROD);

        when(apiKeyRepository.existsByApplicationIdAndEnvironmentId(2L, Environment.PROD.getId()))
                .thenReturn(false);

        assertTrue(validator.isValid(dto, null));
    }
}
