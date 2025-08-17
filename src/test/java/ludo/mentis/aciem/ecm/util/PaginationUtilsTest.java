package ludo.mentis.aciem.ecm.util;

import jakarta.servlet.http.HttpServletRequest;
import ludo.mentis.aciem.ecm.model.PaginationModel;
import ludo.mentis.aciem.ecm.model.PaginationStep;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class PaginationUtilsTest {

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    void testGetPaginationModel_NullPage() {
        assertNull(PaginationUtils.getPaginationModel(null));
    }

    @Test
    void testGetPaginationModel_EmptyPage() {
        Page<String> emptyPage = Page.empty();
        PaginationModel paginationModel = PaginationUtils.getPaginationModel(emptyPage);
        assertNull(paginationModel);
    }

    @Test
    void testGetPaginationModel_SinglePage() {
        Page<String> singlePage = new PageImpl<>(List.of("item1", "item2", "item3"), PageRequest.of(0, 3), 3);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("filter")).thenReturn(null);

        PaginationModel paginationModel = PaginationUtils.getPaginationModel(singlePage);

        assertNotNull(paginationModel);
        assertEquals(3, paginationModel.getSteps().size());
        assertEquals("Item 1 - 3 of 3", paginationModel.getElements());

        PaginationStep previousStep = paginationModel.getSteps().get(0);
        assertTrue(previousStep.isDisabled());
        assertEquals("Previous", previousStep.getLabel());

        PaginationStep nextStep = paginationModel.getSteps().get(2);
        assertTrue(nextStep.isDisabled());
        assertEquals("Next", nextStep.getLabel());
    }

    @Test
    void testGetPaginationModel_MultiplePages() {
        Page<String> multiplePages = new PageImpl<>(List.of("item1", "item2", "item3"), PageRequest.of(1, 3), 9);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("filter")).thenReturn(null);

        PaginationModel paginationModel = PaginationUtils.getPaginationModel(multiplePages);

        assertNotNull(paginationModel);
        assertEquals(5, paginationModel.getSteps().size());
        assertEquals("Item 4 - 6 of 9", paginationModel.getElements());

        PaginationStep previousStep = paginationModel.getSteps().get(0);
        assertFalse(previousStep.isDisabled());
        assertEquals("Previous", previousStep.getLabel());

        PaginationStep nextStep = paginationModel.getSteps().get(4);
        assertFalse(nextStep.isDisabled());
        assertEquals("Next", nextStep.getLabel());
    }

    @Test
    void testGetStepUrl() {
        Page<String> page = new PageImpl<>(List.of("item1", "item2", "item3"), PageRequest.of(0, 3), 3);
        when(request.getParameter("sort")).thenReturn("name,asc");
        when(request.getParameter("filter")).thenReturn("active");

        String stepUrl = PaginationUtils.getStepUrl(page, 1);
        assertEquals("?page=1&size=3&sort=name,asc&filter=active", stepUrl);
    }

    @Test
    void testGetStepUrl_NoSortOrFilter() {
        Page<String> page = new PageImpl<>(List.of("item1", "item2", "item3"), PageRequest.of(0, 3), 3);
        when(request.getParameter("sort")).thenReturn(null);
        when(request.getParameter("filter")).thenReturn(null);

        String stepUrl = PaginationUtils.getStepUrl(page, 1);
        assertEquals("?page=1&size=3", stepUrl);
    }
}