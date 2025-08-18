package ludo.mentis.aciem.ecm.rest;

import ludo.mentis.aciem.ecm.exception.NotFoundException;
import ludo.mentis.aciem.ecm.model.CredentialType;
import ludo.mentis.aciem.ecm.model.Environment;
import ludo.mentis.aciem.ecm.service.CredentialRestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CredentialRestControllerTest {

    private CredentialRestService credentialRestService;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        credentialRestService = Mockito.mock(CredentialRestService.class);
        var controller = new CredentialRestController(credentialRestService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .build();
    }

    @Test
    void getPassword_shouldReturn200WithPassword_whenFound() throws Exception {
        when(credentialRestService.getPassword(any())).thenReturn(Optional.of("s3cr3t!"));

        mockMvc.perform(get("/api/v1/credential")
                        .param("appCode", "ERP")
                        .param("environment", Environment.DEV.name())
                        .param("credentialType", CredentialType.DATABASE.name())
                        .param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.password").value("s3cr3t!"))
                .andExpect(jsonPath("$.exception").doesNotExist());
    }

    @Test
    void getPassword_shouldReturn404_whenOptionalEmpty() throws Exception {
        when(credentialRestService.getPassword(any())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/credential")
                        .param("appCode", "ERP")
                        .param("environment", Environment.QA.name())
                        .param("credentialType", CredentialType.API_KEY.name())
                        .param("username", "alice"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPassword_shouldReturn404_withExceptionBody_whenNotFoundExceptionThrown() throws Exception {
        when(credentialRestService.getPassword(any())).thenThrow(new NotFoundException("Business app not found with code: `ERP`."));

        mockMvc.perform(get("/api/v1/credential")
                        .param("appCode", "ERP")
                        .param("environment", Environment.PROD.name())
                        .param("credentialType", CredentialType.WINDOWS.name())
                        .param("username", "bob"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception", containsString("Business app not found")))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getPassword_shouldReturn400_withValidationErrors_whenMissingRequiredParams() throws Exception {
        // Missing appCode and username -> @NotBlank violations
        mockMvc.perform(get("/api/v1/credential")
                        .param("environment", Environment.UAT.name())
                        .param("credentialType", CredentialType.LINUX.name()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.validationErrors.appCode").exists())
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }

    @Test
    void getPassword_shouldReturn500_withGenericMessage_whenUnexpectedException() throws Exception {
        when(credentialRestService.getPassword(any())).thenThrow(new RuntimeException("boom"));

        mockMvc.perform(get("/api/v1/credential")
                        .param("appCode", "ERP")
                        .param("environment", Environment.DEV.name())
                        .param("credentialType", CredentialType.DATABASE.name())
                        .param("username", "john"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.exception").value("Internal server error. Check server logs for details."));
    }
}
