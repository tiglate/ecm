package ludo.mentis.aciem.ecm.util;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;

import java.util.Map;

/**
 * Utility class for handling sort links.
 * <p>
 * This class provides methods to add sort attributes to a model and build sort links.
 * The sort attributes are used to display sort links in the view, and the sort links
 * are used to sort the data in the controller.
 * </p>
 * <p>
 * The class is designed to be instantiated with a UriBuilder to build the sort links.
 * </p>
 */
public class SortUtils {

    private UriBuilder uriComponentsBuilder;

    public SortUtils() {
        this.uriComponentsBuilder = null;
    }

    /**
     * Constructor that uses the provided UriBuilder to build the sort links.
     *
     * @param uriComponentsBuilder the UriBuilder to use
     */
    public SortUtils(UriBuilder uriComponentsBuilder) {
        this.uriComponentsBuilder = uriComponentsBuilder;
    }

    /**
     * Adds sort attributes to the model for each property in the sortAttributes map.
     *
     * @param model          the model to add the sort attributes to
     * @param sort           the current sort order
     * @param pageable       the current pageable
     * @param sortAttributes a map of property names to attribute names
     * @return the sort order
     */
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

    /**
     * Gets the sort order from the sort string.
     *
     * @param sort the sort string
     * @return the sort order
     */
    Sort getSortOrder(String sort) {
        if (sort != null && !sort.isEmpty()) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = sortParts[1].equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            return Sort.by(direction, sortParts[0]);
        }
        return Sort.unsorted();
    }

    /**
     * Builds a sort link for the given property.
     *
     * @param property the property to sort by
     * @param currentSort the current sort order
     * @param pageable the current pageable
     * @return the sort link
     */
    String buildSortLink(String property, String currentSort, Pageable pageable) {
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
                .build()
                .toString();
    }

    /**
     * Gets the sort direction for the given property.
     *
     * @param property the property to get the sort direction for
     * @param currentSort the current sort order
     * @return the sort direction
     */
    String getSortDirection(String property, String currentSort) {
        if (currentSort != null && currentSort.startsWith(property + ",")) {
            return currentSort.endsWith(",asc") ? "desc" : "asc";
        }
        return "asc"; // Default to ascending if not sorted
    }
}