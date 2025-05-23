package com.epam.capstone.mapper.row;

import com.epam.capstone.model.Status;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusRowMapper implements RowMapper<Status> {
    @Override
    public Status mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Status.builder()
                .statusId(rs.getShort("status_id"))
                .name(rs.getString("name"))
                .build();
    }
}
