package com.epam.capstone.repository;

import com.epam.capstone.model.Status;
import com.epam.capstone.repository.dao.StatusDao;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatusRepositoryTest {

    @Mock
    private StatusDao dao;

    @Mock
    private CustomJdbcTemplate jdbc;

    private StatusRepository repo;

    @BeforeEach
    void setUp() {
        repo = new StatusRepository(dao, jdbc);
    }

    @Test
    void findById_present() {
        Status s = new Status();
        s.setStatusId((short) 1);
        when(dao.findById((short)1)).thenReturn(s);

        Optional<Status> opt = repo.findById((short)1);
        assertTrue(opt.isPresent());
        assertSame(s, opt.get());
    }

    @Test
    void findById_exception() {
        when(dao.findById(anyShort())).thenThrow(new RuntimeException());
        assertTrue(repo.findById((short)5).isEmpty());
    }

    @Test
    void existsById_true() {
        when(dao.findById((short)2)).thenReturn(new Status());
        assertTrue(repo.existsById((short)2));
    }

    @Test
    void existsById_exception() {
        when(dao.findById(anyShort())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById((short)3));
    }

    @Test
    void count_returnsSize() {
        when(dao.findAll()).thenReturn(List.of(new Status(), new Status(), new Status()));
        assertEquals(3L, repo.count());
    }

    @Test
    void count_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void save_insertWhenNull() {
        Status s = new Status();
        s.setStatusId(null);

        Status out = repo.save(s);
        verify(dao).save(s);
        assertSame(s, out);
    }

    @Test
    void save_updateWhenPresent() {
        Status s = new Status();
        s.setStatusId((short)10);

        Status out = repo.save(s);
        verify(dao).update(s);
        assertSame(s, out);
    }

    @Test
    void save_exception() {
        Status s = new Status();
        doThrow(new RuntimeException("boom"))
                .when(dao).save(any(Status.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(s));
        assertTrue(ex.getMessage().contains("Could not save Status"));
    }

    @Test
    void deleteById_existing() {
        Status s = new Status();
        when(dao.findById((short)4)).thenReturn(s);

        repo.deleteById((short)4);
        verify(dao).delete(s);
    }

    @Test
    void deleteById_nonExisting() {
        when(dao.findById((short)5)).thenReturn(null);
        repo.deleteById((short)5);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        Status s = new Status();
        when(dao.findById(anyShort())).thenReturn(s);
        doThrow(new RuntimeException("err"))
                .when(dao).delete(any(Status.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById((short)6));
        assertTrue(ex.getMessage().contains("Could not delete Status"));
    }

    @Test
    void findByName_success() {
        Status s = new Status();
        when(jdbc.queryForObject(anyString(), any(), eq("ACTIVE"))).thenReturn(s);

        Optional<Status> opt = repo.findByName("ACTIVE");
        assertTrue(opt.isPresent());
        assertSame(s, opt.get());
    }

    @Test
    void findByName_exception() {
        when(jdbc.queryForObject(anyString(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByName("X").isEmpty());
    }

    @Test
    void findAll_list_exception() {
        when(jdbc.query(anyString(), any()))
                .thenThrow(new RuntimeException());
        List<Status> result = repo.findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findByNameContaining_delegates() {
        Pageable pg = PageRequest.of(0, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Status> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                    eq(jdbc),
                    anyString(),
                    anyString(),
                    any(),
                    any(),
                    eq("kw"),
                    eq(pg)
            )).thenReturn(fake);

            assertSame(fake, repo.findByNameContaining("kw", pg));
        }
    }
}
