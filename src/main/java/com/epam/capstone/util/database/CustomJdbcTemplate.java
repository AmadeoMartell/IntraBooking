package com.epam.capstone.util.database;

import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class CustomJdbcTemplate {

    private final DataSource dataSource;

    public CustomJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public int update(@Language("SQL") String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindParameters(ps, params);
            return ps.executeUpdate();

        } catch (SQLException ex) {
            log.error("Error executing update [{}] with params {}: {}", sql, Arrays.toString(params), ex.getMessage());
            throw new RuntimeException("Error executing update", ex);
        }
    }

    public <T> T queryForObject(@Language("SQL") String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> results = query(sql, rowMapper, params);
        if (results.isEmpty()) {
            throw new RuntimeException("Expected one result, but got none");
        }
        if (results.size() > 1) {
            throw new RuntimeException("Expected one result, but got " + results.size());
        }
        return results.getFirst();
    }

    public <T> List<T> query(@Language("SQL") String sql, RowMapper<T> rowMapper, Object... params) {
        List<T> resultList = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            bindParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                int rowNum = 0;
                while (rs.next()) {
                    resultList.add(rowMapper.mapRow(rs, rowNum++));
                }
            }
            return resultList;

        } catch (SQLException ex) {
            log.error("Error executing query [{}] with params {}: {}", sql, Arrays.toString(params), ex.getMessage());
            throw new RuntimeException("Error executing query", ex);
        }
    }

    private void bindParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}