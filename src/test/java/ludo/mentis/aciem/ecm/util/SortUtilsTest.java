package ludo.mentis.aciem.ecm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SortUtilsTest {

    private SortUtils sortUtils;

    private Model model;

    @BeforeEach
    void setUp() {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(URI.create("/test"));
        sortUtils = new SortUtils(uriBuilder);
        model = mock(Model.class);
    }

    @Test
    void testAddSortAttributesToModel() {
        Pageable pageable = PageRequest.of(0, 10);
        Map<String, String> sortAttributes = Map.of("name", "name");

        Sort sortOrder = sortUtils.addSortAttributesToModel(model, "name,asc", pageable, sortAttributes);

        verify(model).addAttribute("nameLink", "/test?sort=name,desc&page=0");
        verify(model).addAttribute("nameDirection", "desc");
        assertEquals(Sort.by(Sort.Direction.ASC, "name"), sortOrder);
    }

    @Test
    void testGetSortOrder() {
        Sort sortOrder = sortUtils.getSortOrder("name,asc");
        assertEquals(Sort.by(Sort.Direction.ASC, "name"), sortOrder);

        sortOrder = sortUtils.getSortOrder("name,desc");
        assertEquals(Sort.by(Sort.Direction.DESC, "name"), sortOrder);

        sortOrder = sortUtils.getSortOrder(null);
        assertEquals(Sort.unsorted(), sortOrder);
    }

    @Test
    void testBuildSortLink() {
        Pageable pageable = PageRequest.of(0, 10);
        String sortLink = sortUtils.buildSortLink("name", "name,asc", pageable);
        assertEquals("/test?sort=name,desc&page=0", sortLink);

        sortLink = sortUtils.buildSortLink("name", "name,desc", pageable);
        assertEquals("/test?sort=name,asc&page=0", sortLink);

        sortLink = sortUtils.buildSortLink("name", null, pageable);
        assertEquals("/test?sort=name,asc&page=0", sortLink);
    }

    @Test
    void testGetSortDirection() {
        String sortDirection = sortUtils.getSortDirection("name", "name,asc");
        assertEquals("desc", sortDirection);

        sortDirection = sortUtils.getSortDirection("name", "name,desc");
        assertEquals("asc", sortDirection);

        sortDirection = sortUtils.getSortDirection("name", null);
        assertEquals("asc", sortDirection);
    }
}