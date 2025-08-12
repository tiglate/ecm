package ludo.mentis.aciem.ecm.util;

import ludo.mentis.aciem.ecm.model.PaginationModel;
import ludo.mentis.aciem.ecm.model.PaginationStep;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PaginationUtils {

    private PaginationUtils() {
    }

    private static String getStepUrl(final Page<?> page, final int targetPage) {
        String stepUrl = "?page=" + targetPage + "&size=" + page.getSize();
        if (WebUtils.getRequest().getParameter("sort") != null) {
            stepUrl += "&sort=" + WebUtils.getRequest().getParameter("sort");
        }
        if (WebUtils.getRequest().getParameter("filter") != null) {
            stepUrl += "&filter=" + WebUtils.getRequest().getParameter("filter");
        }
        return stepUrl;
    }

    public static PaginationModel getPaginationModel(final Page<?> page) {
        if (page.isEmpty()) {
            return null;
        }

        final ArrayList<PaginationStep> steps = new ArrayList<>();
        final PaginationStep previous = new PaginationStep();
        previous.setDisabled(!page.hasPrevious());
        previous.setLabel(WebUtils.getMessage("pagination.previous"));
        previous.setUrl(getStepUrl(page, page.previousOrFirstPageable().getPageNumber()));
        steps.add(previous);
        // find a range of up to 5 pages around the current active page
        final int startAt = Math.max(0, Math.min(page.getNumber() - 2, page.getTotalPages() - 5));
        final int endAt = Math.min(startAt + 5, page.getTotalPages());
        for (int i = startAt; i < endAt; i++) {
            final PaginationStep step = new PaginationStep();
            step.setActive(i == page.getNumber());
            step.setLabel("" + (i + 1));
            step.setUrl(getStepUrl(page, i));
            steps.add(step);
        }
        final PaginationStep next = new PaginationStep();
        next.setDisabled(!page.hasNext());
        next.setLabel(WebUtils.getMessage("pagination.next"));
        next.setUrl(getStepUrl(page, page.nextOrLastPageable().getPageNumber()));
        steps.add(next);

        final long rangeStart = (long) page.getNumber() * page.getSize() + 1L;
        final long rangeEnd = Math.min(rangeStart + page.getSize() - 1, page.getTotalElements());
        final String range = rangeStart == rangeEnd ? "" + rangeStart : rangeStart + " - " + rangeEnd;
        final PaginationModel paginationModel = new PaginationModel();
        paginationModel.setSteps(steps);
        paginationModel.setElements(WebUtils.getMessage("pagination.elements", range, page.getTotalElements()));
        return paginationModel;
    }
}
