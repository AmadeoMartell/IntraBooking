package com.epam.capstone.repository.helper;

import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class QueryContainsMatcher {

    /**
     * Executes a COUNT(*) and a paged SELECT with ILIKE %kw% binding.
     *
     * @param jdbc        your CustomJdbcTemplate
     * @param countSql    SQL to count matching rows (expects a single ILIKE placeholder)
     * @param pageSql     SQL to select matching rows (expects ILIKE placeholder, then LIMIT, OFFSET)
     * @param countMapper SingleColumnRowMapper<Long> (for COUNT(*))
     * @param rowMapper   RowMapper<T> for your entity
     * @param kw          the keyword to match (will be wrapped in %…%)
     * @param pg          the Pageable (pageSize + offset)
     * @param <T>         entity type
     * @return a PageImpl<T> containing the results
     */
    public static <T> Page<T> findByQueryContaining(
            CustomJdbcTemplate jdbc,
            String countSql,
            String pageSql,
            SingleColumnRowMapper<Long> countMapper,
            RowMapper<T> rowMapper,
            String kw,
            Pageable pg
    ) {
        try {
            String pattern = "%" + kw + "%";
            Long total = jdbc.queryForObject(countSql, countMapper, pattern);
            List<T> content = jdbc.query(
                    pageSql,
                    rowMapper,
                    pattern,
                    pg.getPageSize(),
                    pg.getOffset()
            );
            return new PageImpl<>(content, pg, total != null ? total : 0L);
        } catch (Exception ex) {
            return new PageImpl<>(Collections.emptyList(), pg, 0L);
        }
    }

    public static <T> Page<T> pageList(
            List<T> all,
            Pageable pg
    ) {
        if (all == null) {
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
        int total = all.size();
        int start = (int) pg.getOffset();
        if (start > total) {
            return new PageImpl<>(Collections.emptyList(), pg, total);
        }
        int end = Math.min(start + pg.getPageSize(), total);
        return new PageImpl<>(all.subList(start, end), pg, total);
    }
}
