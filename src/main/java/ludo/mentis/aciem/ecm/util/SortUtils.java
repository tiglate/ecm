package ludo.mentis.aciem.ecm.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

import java.util.Map;

public class SortUtils {

    private UriBuilder uriComponentsBuilder;

    public SortUtils() {
        this.uriComponentsBuilder = null;
    }

    public SortUtils(UriBuilder uriComponentsBuilder) {
        this.uriComponentsBuilder = uriComponentsBuilder;
    }

    public Sort addSortAttributesToModel(Model model, String sort, Pageable pageable, Map<String, String> sortAttributes) {
        Sort sortOrder = getSortOrder(sort);
        sortAttributes.forEach((property, attributeName) -> {
            String sortLink = buildSortLink(property, sort, pageable);
            String sortDirection = getSortDirection(property, sort);
            model.addAttribute(attributeName + "Link", sortLink);
            model.addAttribute(attributeName + "Direction", sortDirection);
        });
        return sortOrder;
    }

    private Sort getSortOrder(String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return Sort.by(direction, sortParts[0]);
        }
        return Sort.unsorted();
    }

    private String buildSortLink(String property, String currentSort, Pageable pageable) {
        String direction = "asc";
        if (currentSort != null && currentSort.startsWith(property + ",")) {
            direction = currentSort.endsWith(",asc") ? "desc" : "asc";
        }
        if (uriComponentsBuilder == null) {
            uriComponentsBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
        }
        return uriComponentsBuilder
                .replaceQueryParam("sort", property + "," + direction)
                .replaceQueryParam("page", pageable.getPageNumber())
                .toUriString();
    }

    private String getSortDirection(String property, String currentSort) {
        if (currentSort != null && currentSort.startsWith(property + ",")) {
            return currentSort.endsWith(",asc") ? "desc" : "asc";
        }
        return "asc"; // Default to ascending if not sorted
    }
}