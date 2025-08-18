package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.ecm.model.BusinessAppDTO;
import ludo.mentis.aciem.ecm.service.BusinessAppService;
import ludo.mentis.aciem.ecm.util.FlashMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BusinessAppControllerTest {

    private BusinessAppController controller;

    @Mock
    private BusinessAppService businessAppService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new BusinessAppController(businessAppService);
    }

    @Test
    void view_shouldAddBusinessAppToModelAndReturnView() {
        BusinessAppDTO dto = new BusinessAppDTO();
        when(businessAppService.get(1L)).thenReturn(dto);

        String view = controller.view(1L, model);

        assertThat(view).isEqualTo("businessApp/view");
        verify(model).addAttribute("businessApp", dto);
    }

    @Test
    void editGet_shouldAddBusinessAppToModelAndReturnEditPage() {
        BusinessAppDTO dto = new BusinessAppDTO();
        when(businessAppService.get(2L)).thenReturn(dto);

        String view = controller.edit(2L, model);

        assertThat(view).isEqualTo("businessApp/edit");
        verify(model).addAttribute("businessApp", dto);
    }

    @Test
    void addGet_shouldReturnAddView() {
        String view = controller.add(new BusinessAppDTO());
        assertThat(view).isEqualTo("businessApp/add");
    }

    @Test
    void addPost_withErrors_shouldReturnAddViewAndNotCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.add(new BusinessAppDTO(), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("businessApp/add");
        verify(businessAppService, never()).create(any());
    }

    @Test
    void addPost_withoutErrors_shouldCreateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.add(new BusinessAppDTO(), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/applications");
        verify(businessAppService).create(any(BusinessAppDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }

    @Test
    void editPost_withErrors_shouldReturnEditViewAndNotUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.edit(5L, new BusinessAppDTO(), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("businessApp/edit");
        verify(businessAppService, never()).update(anyLong(), any());
    }

    @Test
    void editPost_withoutErrors_shouldUpdateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.edit(6L, new BusinessAppDTO(), bindingResult, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/applications");
        verify(businessAppService).update(eq(6L), any(BusinessAppDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }
/*
    @Test
    void deletePost_withReferencedWarning_shouldNotDeleteAndAddErrorFlash() {
        ReferencedWarning warning = new ReferencedWarning();
        warning.setKey("test.key");
        when(businessAppService.getReferencedWarning(9L)).thenReturn(warning);

        String view = controller.delete(9L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/applications");
        verify(businessAppService, never()).delete(anyLong());
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_ERROR), any());
    }
*/
    @Test
    void deletePost_withoutReferencedWarning_shouldDeleteAndAddSuccessFlash() {
        when(businessAppService.getReferencedWarning(10L)).thenReturn(null);

        String view = controller.delete(10L, redirectAttributes);

        assertThat(view).isEqualTo("redirect:/applications");
        verify(businessAppService).delete(10L);
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }
}
