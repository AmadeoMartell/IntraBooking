package com.epam.capstone.util;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for calculating pagination parameters (startPage, endPage, and list of pageNumbers).
 */
public class PaginationUtil {

    /**
     * Builds a {@link PaginationInfo} object based on the given Spring Data {@code page}.
     *
     * @param page       the Page object containing content and metadata
     * @param windowSize how many page links should be visible around the current page.
     *                   For example, if windowSize = 3, then when totalPages > 3,
     *                   you will show up to 3 page links (current, one before, one after).
     * @return a {@link PaginationInfo} containing all calculated pagination fields
     */
    public static PaginationInfo getPaginationInfo(Page<?> page, int windowSize) {
        int currentPage = page.getNumber();
        int totalPages  = page.getTotalPages();
        int pageSize    = page.getSize();

        // Handle case when there are no pages or only one page
        if (totalPages == 0) {
            return new PaginationInfo(0, 0, pageSize, 0, 0, List.of(0));
        }

        // Ensure currentPage is within valid bounds
        if (currentPage >= totalPages) {
            currentPage = totalPages - 1;
        }
        if (currentPage < 0) {
            currentPage = 0;
        }

        int startPage;
        int endPage;

        if (totalPages <= windowSize) {
            // If total pages are fewer than or equal to windowSize, show all pages
            startPage = 0;
            endPage   = totalPages - 1;
        } else {
            // Otherwise, create a sliding window around currentPage
            int halfWindow = windowSize / 2;
            if (currentPage <= halfWindow) {
                // If currentPage is near the beginning, shift window to the left
                startPage = 0;
                endPage   = windowSize - 1;
            } else if (currentPage + halfWindow >= totalPages) {
                // If currentPage is near the end, shift window to the right
                startPage = totalPages - windowSize;
                endPage   = totalPages - 1;
            } else {
                // Center the window around currentPage
                startPage = currentPage - halfWindow;
                endPage   = currentPage + halfWindow;
                // If windowSize is even, adjust so that window covers exactly windowSize pages
                if (windowSize % 2 == 0) {
                    endPage = startPage + windowSize - 1;
                }
            }
        }

        // Build the list of page numbers (0-based) for rendering links
        List<Integer> pageNumbers = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            pageNumbers.add(i);
        }

        return new PaginationInfo(currentPage, totalPages, pageSize, startPage, endPage, pageNumbers);
    }
}
