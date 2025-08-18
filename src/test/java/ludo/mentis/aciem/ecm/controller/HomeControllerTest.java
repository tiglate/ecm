package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.ecm.util.FlashMessages;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HomeControllerTest {

    private HomeController controller;

    @Mock
    private Model model;

    @Mock
    private SecurityContext securityContext;

    AutoCloseable mocks;

    @BeforeEach
    void setUp() {
        mocks = MockitoAnnotations.openMocks(this);
        controller = new HomeController();
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
        mocks.close();
    }

    @Test
    void index_shouldReturnViewAndIncludeSortedAuthorities() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_Z"), new SimpleGrantedAuthority("ROLE_A")));
        when(securityContext.getAuthentication()).thenReturn(auth);

        String view = controller.index(null, model);

        assertThat(view).isEqualTo("home/index");
        verify(model).addAttribute("authorities", List.of("ROLE_A", "ROLE_Z"));
    }

    @Test
    void index_withLogoutSuccessTrue_shouldAddInfoMessage() {
        when(securityContext.getAuthentication()).thenReturn(null);
        controller.index(true, model);
        verify(model).addAttribute(FlashMessages.MSG_INFO, "Your logout was successful.");
    }
}
