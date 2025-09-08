package ludo.mentis.aciem.ecm.validation;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.repos.CredentialRepository;
import ludo.mentis.aciem.ecm.service.CredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class CredentialUniqueValidatorTest {

    private CredentialRepository credentialRepository;
    private CredentialService credentialService;
    private HttpServletRequest request;
    private CredentialUnique.CredentialUniqueValidator validator;

    @BeforeEach
    void setUp() {
        credentialRepository = mock(CredentialRepository.class);
        credentialService = mock(CredentialService.class);
        request = mock(HttpServletRequest.class);
        validator = new CredentialUnique.CredentialUniqueValidator(credentialRepository, credentialService, request);
    }

    @Test
    void isValid_NullDto_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_MissingCriticalFields_ReturnsTrue() {
        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(1L);
        dto.setEnvironment(null);
        dto.setCredentialType(CredentialType.DATABASE);
        assertTrue(validator.isValid(dto, null));

        CredentialDTO dto2 = new CredentialDTO();
        dto2.setApplicationId(1L);
        dto2.setEnvironment(Environment.DEV);
        dto2.setCredentialType(null);
        assertTrue(validator.isValid(dto2, null));
    }

    @Test
    void isValid_CreateWithBlankUsername_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(1L);
        dto.setEnvironment(Environment.QA);
        dto.setCredentialType(CredentialType.LINUX);
        dto.setUsername(" ");
        dto.setVersion(null);
        dto.setEnabled(null);
        assertTrue(validator.isValid(dto, null));
        verify(credentialRepository, never()).existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(any(), any(), any(), any(), any(), any());
    }

    @Test
    void isValid_CreateDuplicateComposite_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(2L);
        dto.setEnvironment(Environment.UAT);
        dto.setCredentialType(CredentialType.WINDOWS);
        dto.setUsername("john");
        dto.setVersion(null); // will default to 1
        dto.setEnabled(null); // will default to TRUE

        when(credentialRepository.existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                2L, Environment.UAT.getId(), CredentialType.WINDOWS.getId(), "john", 1, true
        )).thenReturn(true);

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void isValid_CreateUniqueComposite_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE)).thenReturn(null);
        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(3L);
        dto.setEnvironment(Environment.PROD);
        dto.setCredentialType(CredentialType.API_KEY);
        dto.setUsername("service");
        dto.setVersion(5);
        dto.setEnabled(false);

        when(credentialRepository.existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                3L, Environment.PROD.getId(), CredentialType.API_KEY.getId(), "service", 5, false
        )).thenReturn(false);

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_UpdateVersionIncrement_NotExisting_ReturnsTrue() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "9"));

        CredentialDTO existing = new CredentialDTO();
        existing.setId(9L);
        existing.setApplicationId(4L);
        existing.setEnvironment(Environment.DEV);
        existing.setCredentialType(CredentialType.DATABASE);
        existing.setUsername("db_user");
        existing.setVersion(3);
        existing.setEnabled(false);
        when(credentialService.get(9L)).thenReturn(existing);

        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(4L);
        dto.setEnvironment(Environment.DEV);
        dto.setCredentialType(CredentialType.DATABASE);

        // Expect check with version = existing.version + 1 = 4 and enabled = true
        when(credentialRepository.existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                4L, Environment.DEV.getId(), CredentialType.DATABASE.getId(), "db_user", 4, true
        )).thenReturn(false);

        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_UpdateDuplicateComposite_ReturnsFalse() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "10"));

        CredentialDTO existing = new CredentialDTO();
        existing.setId(10L);
        existing.setApplicationId(5L);
        existing.setEnvironment(Environment.QA);
        existing.setCredentialType(CredentialType.JWT_TOKEN);
        existing.setUsername("jwt_user");
        existing.setVersion(1);
        existing.setEnabled(true);
        when(credentialService.get(10L)).thenReturn(existing);

        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(5L);
        dto.setEnvironment(Environment.QA);
        dto.setCredentialType(CredentialType.JWT_TOKEN);

        when(credentialRepository.existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                5L, Environment.QA.getId(), CredentialType.JWT_TOKEN.getId(), "jwt_user", 2, true
        )).thenReturn(true);

        assertFalse(validator.isValid(dto, null));
    }

    @Test
    void isValid_UpdateWhenExistingHasNullVersion_ChecksWith1() {
        when(request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(Map.of("id", "11"));

        CredentialDTO existing = new CredentialDTO();
        existing.setId(11L);
        existing.setApplicationId(6L);
        existing.setEnvironment(Environment.UAT);
        existing.setCredentialType(CredentialType.OTHER);
        existing.setUsername("x");
        existing.setVersion(null); // null -> defaults to 1 during update
        when(credentialService.get(11L)).thenReturn(existing);

        CredentialDTO dto = new CredentialDTO();
        dto.setApplicationId(6L);
        dto.setEnvironment(Environment.UAT);
        dto.setCredentialType(CredentialType.OTHER);

        when(credentialRepository.existsByApplicationIdAndEnvironmentIdAndCredentialTypeIdAndUsernameAndVersionAndEnabled(
                6L, Environment.UAT.getId(), CredentialType.OTHER.getId(), "x", 1, true
        )).thenReturn(false);

        assertTrue(validator.isValid(dto, null));
    }
}
