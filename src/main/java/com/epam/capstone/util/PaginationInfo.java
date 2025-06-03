package com.epam.capstone.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * DTO/VO that holds all necessary parameters for rendering a pagination block.
 */
@RequiredArgsConstructor
@Getter
public class PaginationInfo {

    /** Current page index (0-based). */
    private final int currentPage;

    /** Total number of pages. */
    private final int totalPages;

    /** Page size (number of items per page). */
    private final int pageSize;

    /** Leftmost page index in the visible window. */
    private final int startPage;

    /** Rightmost page index in the visible window. */
    private final int endPage;

    /** List of visible page indices (0-based) for rendering page links. */
    private final List<Integer> pageNumbers;

}
