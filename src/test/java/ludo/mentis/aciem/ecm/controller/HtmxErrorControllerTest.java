package ludo.mentis.aciem.ecm.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.web.servlet.ModelAndView;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class HtmxErrorControllerTest {

    @Mock
    private BasicErrorController basicErrorController;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private HtmxErrorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new HtmxErrorController(basicErrorController);
    }

    @Test
    void errorHtmx_shouldDelegateToBasicErrorController_andReturnItsModelAndView() {
        ModelAndView mv = new ModelAndView("error/test");
        when(basicErrorController.errorHtml(request, response)).thenReturn(mv);

        ModelAndView result = controller.errorHtmx(request, response);

        assertThat(result).isSameAs(mv);
        verify(basicErrorController, times(1)).errorHtml(request, response);
    }
}
