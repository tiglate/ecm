package ludo.mentis.aciem.ecm.util;

import ludo.mentis.aciem.ecm.model.PaginationModel;
import ludo.mentis.aciem.ecm.model.PaginationStep;
import org.springframework.data.domain.Page;

import java.util.ArrayList;


/**
 * Utility class for handling pagination tasks.
 * <p>
 * This class provides static methods to generate pagination models based on
 * Spring Data Page objects. It creates pagination steps for navigating through
 * the pages and a summary of the current range of items being displayed.
 * </p>
 * <p>
 * The class is designed to be non-instantiable and contains a private constructor
 * to prevent instantiation.
 * </p>
 */
public class PaginationUtils {

    /**
     * Private constructor to prevent instantiation
     */
    private PaginationUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Generates a PaginationModel object based on the provided Page object.
     * This method creates a pagination model that includes navigation steps
     * (previous, next, and up to 5 pages around the current page) and a summary
     * of the current range of items being displayed.
     *
     * @param page the Page object containing pagination information
     * @return a PaginationModel object with pagination steps and item range summary,
     *         or null if the provided Page object is null or empty
     */
    public static PaginationModel getPaginationModel(final Page<?> page) {
        if (page == null || page.isEmpty()) {
            return null;
        }

        final var previousPageNumber = page.getPageable().isUnpaged() ? 0 : page.previousOrFirstPageable().getPageNumber();
        final var nextPageNumber = page.getPageable().isUnpaged() ? 0 : page.nextOrLastPageable().getPageNumber();

        final var steps = new ArrayList<PaginationStep>();

        final var previous = new PaginationStep();
        previous.setDisabled(!page.hasPrevious());
        previous.setLabel("Previous");
        previous.setUrl(getStepUrl(page, previousPageNumber));
        steps.add(previous);

        // find a range of up to 5 pages around the current active page
        final int startAt = Math.max(0, Math.min(page.getNumber() - 2, page.getTotalPages() - 5));
        final int endAt = Math.min(startAt + 5, page.getTotalPages());

        for (int i = startAt; i < endAt; i++) {
            final var step = new PaginationStep();
            step.setActive(i == page.getNumber());
            step.setLabel("" + (i + 1));
            step.setUrl(getStepUrl(page, i));
            steps.add(step);
        }

        final var next = new PaginationStep();
        next.setDisabled(!page.hasNext());
        next.setLabel("Next");
        next.setUrl(getStepUrl(page, nextPageNumber));
        steps.add(next);

        final var rangeStart = (long) page.getNumber() * page.getSize() + 1L;
        final var rangeEnd = Math.min(rangeStart + page.getSize() - 1, page.getTotalElements());
        final var range = rangeStart == rangeEnd ? "" + rangeStart : rangeStart + " - " + rangeEnd;
        final var paginationModel = new PaginationModel();
        paginationModel.setSteps(steps);
        paginationModel.setElements(String.format("Item %s of %d", range, page.getTotalElements()));
        return paginationModel;
    }

    /**
     * Constructs the URL for a specific pagination step.
     * This method generates a URL with query parameters for the target page and size,
     * and includes optional sorting and filtering parameters if they are present in the request.
     *
     * @param page the Page object containing pagination information
     * @param targetPage the target page number for the pagination step
     * @return a String representing the URL for the pagination step
     * @throws IllegalStateException if the request attributes are not available
     */
    static String getStepUrl(final Page<?> page, final int targetPage) {
        String stepUrl = "?page=" + targetPage + "&size=" + page.getSize();
        if (WebUtils.getRequest().getParameter("sort") != null) {
            stepUrl += "&sort=" + WebUtils.getRequest().getParameter("sort");
        }
        if (WebUtils.getRequest().getParameter("filter") != null) {
            stepUrl += "&filter=" + WebUtils.getRequest().getParameter("filter");
        }
        return stepUrl;
    }
}