package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.commons.web.FlashMessages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HomeControllerTest {

    private HomeController controller;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        controller = new HomeController();
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        if (mocks != null) mocks.close();
    }

    @Test
    void index_withLogoutSuccessTrue_shouldAddInfoMessageAndAuthorities() {
        // Arrange authentication with two authorities out of order
        GrantedAuthority a = () -> "ROLE_Z";
        GrantedAuthority b = () -> "ROLE_A";
        org.springframework.security.core.Authentication auth =
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        "user", "pass", List.of(a, b));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        String view = controller.index(true, model);

        // Assert
        assertThat(view).isEqualTo("home/index");
        verify(model).addAttribute(FlashMessages.MSG_INFO, "Your logout was successful.");
        // Verify sorted authorities added to the model
        verify(model).addAttribute("authorities", List.of("ROLE_A", "ROLE_Z"));
    }

    @Test
    void index_withoutAuthentication_shouldAddEmptyAuthoritiesAndNoInfoMessage() {
        // No authentication in context
        SecurityContextHolder.clearContext();

        String view = controller.index(null, model);

        assertThat(view).isEqualTo("home/index");
        // Should add empty authorities
        verify(model).addAttribute("authorities", List.of());
        // Should not add an info message
        verify(model, never()).addAttribute(eq(FlashMessages.MSG_INFO), any());
    }
}
