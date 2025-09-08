package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.commons.web.PaginationUtils;
import ludo.mentis.aciem.commons.web.SortUtils;
import ludo.mentis.aciem.ecm.model.CredentialDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.CredentialService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CredentialControllerTest {

    private CredentialController controller;

    @Mock
    private SortUtils sortUtils;

    @Mock
    private PaginationUtils paginationUtils;

    @Mock
    private CredentialService credentialService;

    @Mock
    private BusinessAppRepository businessAppRepository;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @Mock
    private RedirectAttributes redirectAttributes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(businessAppRepository.findAll(any(Sort.class))).thenReturn(List.of());
        // Provide default sort behavior used by list()
        when(sortUtils.addSortAttributesToModel(any(), any(), any(), any()))
                .thenReturn(Sort.by(Sort.Order.desc("id")));
        controller = new CredentialController(sortUtils, paginationUtils, credentialService, businessAppRepository);
    }

    @Test
    void list_shouldDefaultEnabledTruePopulateModelAndReturnListView() {
        CredentialDTO filter = new CredentialDTO();
        Pageable pageable = PageRequest.of(0, 20);
        Page<CredentialDTO> page = new PageImpl<>(List.of(), pageable, 0);
        doReturn(page).when(credentialService).findAll(any(CredentialDTO.class), any(Pageable.class));

        String view = controller.list(filter, null, pageable, model);

        assertThat(view).isEqualTo("credential/list");
        // The controller sets enabled to true if null
        assertThat(filter.getEnabled()).isTrue();
        verify(model).addAttribute("credentials", page);
        verify(model).addAttribute("filter", filter);
        verify(model).addAttribute(eq("paginationModel"), any());
    }

    @Test
    void view_shouldAddCredentialToModelAndReturnView() {
        CredentialDTO dto = new CredentialDTO();
        when(credentialService.get(10L)).thenReturn(dto);
        String view = controller.view(10L, model);
        assertThat(view).isEqualTo("credential/view");
        verify(model).addAttribute("credential", dto);
    }

    @Test
    void history_shouldAddCredentialsToModelAndReturnHistoryView() {
        List<CredentialDTO> list = List.of(new CredentialDTO(), new CredentialDTO());
        doReturn(list).when(credentialService).findHistory(7L);
        String view = controller.history(7L, model);
        assertThat(view).isEqualTo("credential/history");
        verify(model).addAttribute("credentials", list);
    }

    @Test
    void editGet_withOldVersion_shouldRedirectAndAddFlashError() {
        CredentialDTO oldVersion = mock(CredentialDTO.class);
        when(oldVersion.getIsLatest()).thenReturn(false);
        when(credentialService.get(3L)).thenReturn(oldVersion);
        String view = controller.edit(3L, model, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/credentials");
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_ERROR), any());
        verify(model, never()).addAttribute(eq("credential"), any());
    }

    @Test
    void editGet_withLatest_shouldReturnEditAndAddModel() {
        CredentialDTO latest = mock(CredentialDTO.class);
        when(latest.getIsLatest()).thenReturn(true);
        when(credentialService.get(4L)).thenReturn(latest);
        String view = controller.edit(4L, model, redirectAttributes);
        assertThat(view).isEqualTo("credential/edit");
        verify(model).addAttribute("credential", latest);
    }

    @Test
    void addGet_shouldReturnAddView() {
        String view = controller.add(new CredentialDTO());
        assertThat(view).isEqualTo("credential/add");
    }

    @Test
    void addPost_withErrors_shouldReturnAddViewAndNotCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = controller.add(new CredentialDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("credential/add");
        verify(credentialService, never()).create(any());
    }

    @Test
    void addPost_withoutErrors_shouldCreateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = controller.add(new CredentialDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/credentials");
        verify(credentialService).create(any(CredentialDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }

    @Test
    void editPost_withErrors_shouldReturnEditViewAndNotUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = controller.edit(12L, new CredentialDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("credential/edit");
        verify(credentialService, never()).update(anyLong(), any());
    }

    @Test
    void editPost_withoutErrors_shouldUpdateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = controller.edit(13L, new CredentialDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/credentials");
        verify(credentialService).update(eq(13L), any(CredentialDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }

    @Test
    void deletePost_shouldDeleteAndRedirect() {
        String view = controller.delete(99L, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/credentials");
        verify(credentialService).delete(99L);
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }
}
