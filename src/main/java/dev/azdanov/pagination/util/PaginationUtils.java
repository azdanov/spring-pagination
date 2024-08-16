package dev.azdanov.pagination.util;

import org.springframework.data.web.PagedModel;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Utility class for generating pagination items.
 * Provides methods to calculate and create pagination structures based on current page and total pages.
 */
public final class PaginationUtils {

    /**
     * Number of pages to display in the center of the pagination when using ellipsis.
     */
    private static final int CENTER_PAGES = 3;

    /**
     * Minimum number of pages between the edge and nearest sibling page before showing an ellipsis.
     */
    private static final int ELLIPSIS_THRESHOLD = 2;

    private PaginationUtils() {
    }

    /**
     * Generates a list of {@link PageItem} based on the provided page metadata.
     *
     * @param metadata The page metadata
     * @return List of {@link PageItem} representing the pagination structure
     */
    public static List<PageItem> getPageItems(@NonNull PagedModel.PageMetadata metadata) {
        int currentPage = (int) (metadata.number() + 1);
        int totalPages = (int) metadata.totalPages();

        return calculatePagination(currentPage, totalPages);
    }

    /**
     * Calculates the pagination structure.
     *
     * @param currentPage Current page number
     * @param totalPages  Total number of pages
     * @return List of {@link PageItem} representing the pagination structure
     */
    private static List<PageItem> calculatePagination(int currentPage, int totalPages) {
        if (totalPages <= CENTER_PAGES + ELLIPSIS_THRESHOLD * 2) {
            return createFullPagination(totalPages);
        }

        int startPage = Math.max(currentPage - 1, 1);
        int endPage = Math.min(currentPage + 1, totalPages);

        boolean showLeftEllipsis = startPage > ELLIPSIS_THRESHOLD;
        boolean showRightEllipsis = endPage < totalPages - ELLIPSIS_THRESHOLD;

        if (!showLeftEllipsis && showRightEllipsis) {
            return createLeftPagination(totalPages);
        }
        if (showLeftEllipsis && !showRightEllipsis) {
            return createRightPagination(totalPages);
        }
        if (showLeftEllipsis && showRightEllipsis) {
            return createCenterPagination(startPage, endPage, totalPages);
        }

        return createFullPagination(totalPages);
    }

    /**
     * Creates pagination with ellipsis only on the right side.
     *
     * @param lastPage Last page number
     * @return List of {@link PageItem} for left-side pagination
     */
    private static List<PageItem> createLeftPagination(int lastPage) {
        int end = CENTER_PAGES + ELLIPSIS_THRESHOLD;

        List<PageItem> pages = new ArrayList<>(range(1, end));
        pages.add(new PageItem(end + 1, true));
        pages.add(new PageItem(lastPage, false));

        return pages;
    }

    /**
     * Creates pagination with ellipsis only on the left side.
     *
     * @param lastPage Last page number
     * @return List of {@link PageItem} for right-side pagination
     */
    private static List<PageItem> createRightPagination(int lastPage) {
        int start = lastPage - CENTER_PAGES - 1;

        List<PageItem> pages = new ArrayList<>();
        pages.add(new PageItem(1, false));
        pages.add(new PageItem(start - 1, true));
        pages.addAll(range(start, lastPage));

        return pages;
    }

    /**
     * Creates pagination with ellipsis on both sides.
     *
     * @param startPage Start page number
     * @param endPage   End page number
     * @param lastPage  Last page number
     * @return List of {@link PageItem} for center pagination
     */
    private static List<PageItem> createCenterPagination(int startPage, int endPage, int lastPage) {
        List<PageItem> pages = new ArrayList<>();
        pages.add(new PageItem(1, false));
        pages.add(new PageItem(startPage - 1, true));
        pages.addAll(range(startPage, endPage));
        pages.add(new PageItem(endPage + 1, true));
        pages.add(new PageItem(lastPage, false));

        return pages;
    }

    /**
     * Creates pagination without an ellipsis.
     *
     * @param lastPage Last page number
     * @return List of {@link PageItem} for full pagination
     */
    private static List<PageItem> createFullPagination(int lastPage) {
        return range(1, lastPage);
    }

    /**
     * Generates a list of {@link PageItem} for a range of page numbers.
     * * @param start The starting page number
     *
     * @param start Starting page number
     * @param end   Ending page number
     * @return List of {@link PageItem} for the specified range
     */
    private static List<PageItem> range(int start, int end) {
        return IntStream.rangeClosed(start, end)
            .mapToObj(i -> new PageItem(i, false))
            .toList();
    }
}
