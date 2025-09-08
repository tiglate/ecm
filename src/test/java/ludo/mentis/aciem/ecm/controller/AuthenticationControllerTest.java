package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.ecm.model.AuthenticationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

class AuthenticationControllerTest {

    private AuthenticationController controller;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AuthenticationController();
    }

    @Test
    void login_shouldReturnViewAndAddAuthenticationAttribute() {
        String view = controller.login(null, null, model);
        assertThat(view).isEqualTo("authentication/login");
        ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);
        verify(model).addAttribute(nameCaptor.capture(), valueCaptor.capture());
        assertThat(nameCaptor.getValue()).isEqualTo("authentication");
        assertThat(valueCaptor.getValue()).isInstanceOf(AuthenticationRequest.class);
    }

    @Test
    void login_withLoginRequiredTrue_shouldAddInfoMessage() {
        controller.login(true, null, model);
        verify(model).addAttribute(FlashMessages.MSG_INFO, "Please login to access this area.");
    }

    @Test
    void login_withLoginErrorTrue_shouldAddErrorMessage() {
        controller.login(null, true, model);
        verify(model).addAttribute(FlashMessages.MSG_ERROR, "Your login was not successful. Please try again.");
    }
}
