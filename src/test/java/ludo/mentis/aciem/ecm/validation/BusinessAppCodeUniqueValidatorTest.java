package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.service.BusinessAppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BusinessAppCodeUniqueValidatorTest {

    private BusinessAppService businessAppService;
    private HttpServletRequest request;
    private BusinessAppCodeUnique.BusinessAppCodeUniqueValidator validator;

    @BeforeEach
    void setUp() {
        businessAppService = mock(BusinessAppService.class);
        request = mock(HttpServletRequest.class);
        validator = new BusinessAppCodeUnique.BusinessAppCodeUniqueValidator(businessAppService, request);
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_EditingAndCodeUnchanged_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "5"));
        BusinessAppDTO existing = new BusinessAppDTO();
        existing.setId(5L);
        existing.setCode("ECM-01");
        when(businessAppService.get(5L)).thenReturn(existing);

        assertTrue(validator.isValid("ecm-01", null), "Same code (case-insensitive) on edit should be valid");
        verify(businessAppService, never()).codeExists(anyString());
    }

    @Test
    void isValid_CodeAlreadyExists_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(businessAppService.codeExists("APP-X"))
                .thenReturn(true);

        assertFalse(validator.isValid("APP-X", null));
    }

    @Test
    void isValid_CodeDoesNotExist_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(businessAppService.codeExists("UNIQ-42"))
                .thenReturn(false);

        assertTrue(validator.isValid("UNIQ-42", null));
    }

    @Test
    void isValid_InvalidIdFallsBackToExists_ChecksExistence() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "not-a-number"));
        when(businessAppService.codeExists("DUP-1")).thenReturn(true);

        assertFalse(validator.isValid("DUP-1", null));
        verify(businessAppService).codeExists("DUP-1");
    }
}
