package ludo.mentis.aciem.ecm.controller;

import ludo.mentis.aciem.commons.web.FlashMessages;
import ludo.mentis.aciem.commons.web.PaginationUtils;
import ludo.mentis.aciem.commons.web.SortUtils;
import ludo.mentis.aciem.ecm.model.ApiKeyDTO;
import ludo.mentis.aciem.ecm.repos.BusinessAppRepository;
import ludo.mentis.aciem.ecm.service.ApiKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiKeyControllerTest {

    private ApiKeyController controller;

    @Mock
    private SortUtils sortUtils;

    @Mock
    private PaginationUtils paginationUtils;

    @Mock
    private ApiKeyService apiKeyService;

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
        // prepareContext may be called by MVC typically; in direct invocation we stub repository sort-dependent call
        when(businessAppRepository.findAll(any(Sort.class))).thenReturn(List.of());
        when(sortUtils.addSortAttributesToModel(any(), any(), any(), any())).thenReturn(Sort.by(Sort.Order.desc("id")));
        controller = new ApiKeyController(sortUtils, apiKeyService, paginationUtils, businessAppRepository);
    }

    @Test
    void list_shouldPopulateModelAndReturnListView() {
        ApiKeyDTO filter = new ApiKeyDTO();
        var pageable = PageRequest.of(0, 20);
        var page = new PageImpl<ApiKeyDTO>(List.of(), pageable, 0);
        doReturn(page).when(apiKeyService).findAll(any(ApiKeyDTO.class), any(Pageable.class));

        String view = controller.list(filter, null, pageable, model);

        assertThat(view).isEqualTo("apiKey/list");
        verify(model).addAttribute(eq("apiKeys"), eq(page));
        verify(model).addAttribute(eq("filter"), eq(filter));
        verify(model).addAttribute(eq("paginationModel"), any());
    }

    @Test
    void view_shouldAddApiKeyToModelAndReturnView() {
        ApiKeyDTO dto = new ApiKeyDTO();
        when(apiKeyService.get(1L)).thenReturn(dto);
        String view = controller.view(1L, model);
        assertThat(view).isEqualTo("apiKey/view");
        verify(model).addAttribute("apiKey", dto);
    }

    @Test
    void editGet_shouldAddApiKeyToModelAndReturnEditPage() {
        ApiKeyDTO dto = new ApiKeyDTO();
        when(apiKeyService.get(2L)).thenReturn(dto);
        String view = controller.edit(2L, model);
        assertThat(view).isEqualTo("apiKey/edit");
        verify(model).addAttribute("apiKey", dto);
    }

    @Test
    void addGet_shouldReturnAddView() {
        String view = controller.add(new ApiKeyDTO());
        assertThat(view).isEqualTo("apiKey/add");
    }

    @Test
    void addPost_withErrors_shouldReturnAddViewAndNotCreate() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = controller.add(new ApiKeyDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("apiKey/add");
        verify(apiKeyService, never()).create(any());
    }

    @Test
    void addPost_withoutErrors_shouldCreateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = controller.add(new ApiKeyDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/apiKeys");
        verify(apiKeyService).create(any(ApiKeyDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }

    @Test
    void editPost_withErrors_shouldReturnEditViewAndNotUpdate() {
        when(bindingResult.hasErrors()).thenReturn(true);
        String view = controller.edit(5L, new ApiKeyDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("apiKey/edit");
        verify(apiKeyService, never()).update(anyLong(), any());
    }

    @Test
    void editPost_withoutErrors_shouldUpdateAndRedirect() {
        when(bindingResult.hasErrors()).thenReturn(false);
        String view = controller.edit(6L, new ApiKeyDTO(), bindingResult, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/apiKeys");
        verify(apiKeyService).update(eq(6L), any(ApiKeyDTO.class));
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }

    @Test
    void deletePost_shouldDeleteAndRedirect() {
        String view = controller.delete(9L, redirectAttributes);
        assertThat(view).isEqualTo("redirect:/apiKeys");
        verify(apiKeyService).delete(9L);
        verify(redirectAttributes).addFlashAttribute(eq(FlashMessages.MSG_SUCCESS), any());
    }
}
