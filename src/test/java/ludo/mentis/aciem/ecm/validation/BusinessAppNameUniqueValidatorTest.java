package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.service.BusinessAppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class BusinessAppNameUniqueValidatorTest {

    private BusinessAppService businessAppService;
    private HttpServletRequest request;
    private BusinessAppNameUnique.BusinessAppNameUniqueValidator validator;

    @BeforeEach
    void setUp() {
        businessAppService = mock(BusinessAppService.class);
        request = mock(HttpServletRequest.class);
        validator = new BusinessAppNameUnique.BusinessAppNameUniqueValidator(businessAppService, request);
    }

    @Test
    void isValid_NullValue_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_EditingAndNameUnchanged_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "5"));
        BusinessAppDTO existing = new BusinessAppDTO();
        existing.setId(5L);
        existing.setName("MyApp");
        when(businessAppService.get(5L)).thenReturn(existing);

        assertTrue(validator.isValid("myapp", null), "Same name (case-insensitive) on edit should be valid");
        verify(businessAppService, never()).nameExists(anyString());
    }

    @Test
    void isValid_NameAlreadyExists_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(businessAppService.nameExists("AppX")).thenReturn(true);

        assertFalse(validator.isValid("AppX", null));
    }

    @Test
    void isValid_NameDoesNotExist_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        when(businessAppService.nameExists("UniqueApp")).thenReturn(false);

        assertTrue(validator.isValid("UniqueApp", null));
    }
}
