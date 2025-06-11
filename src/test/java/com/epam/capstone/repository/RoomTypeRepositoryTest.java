package com.epam.capstone.repository;

import com.epam.capstone.model.RoomType;
import com.epam.capstone.repository.dao.RoomTypeDao;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomTypeRepositoryTest {

    @Mock
    private RoomTypeDao dao;

    @Mock
    private CustomJdbcTemplate jdbc;

    private RoomTypeRepository repo;

    @BeforeEach
    void setUp() {
        repo = new RoomTypeRepository(dao, jdbc);
    }

    @Test
    void findById_present() {
        RoomType rt = new RoomType();
        rt.setTypeId(3);
        when(dao.findById(3)).thenReturn(rt);

        Optional<RoomType> opt = repo.findById(3);
        assertTrue(opt.isPresent());
        assertSame(rt, opt.get());
    }

    @Test
    void findById_exception() {
        when(dao.findById(anyInt())).thenThrow(new RuntimeException());
        assertTrue(repo.findById(5).isEmpty());
    }

    @Test
    void findByLocationId_exception() {
        when(jdbc.query(anyString(), any(), anyLong()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByLocationId(7L).isEmpty());
    }

    @Test
    void existsById_true() {
        when(dao.findById(2)).thenReturn(new RoomType());
        assertTrue(repo.existsById(2));
    }

    @Test
    void existsById_exception() {
        when(dao.findById(anyInt())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById(1));
    }

    @Test
    void count_returnsSize() {
        when(dao.findAll()).thenReturn(List.of(new RoomType(), new RoomType(), new RoomType()));
        assertEquals(3L, repo.count());
    }

    @Test
    void count_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void findAll_returnsList() {
        List<RoomType> list = List.of(new RoomType());
        when(dao.findAll()).thenReturn(list);
        assertEquals(list, repo.findAll());
    }

    @Test
    void findAll_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void findAllPage_delegates() {
        Pageable pg = PageRequest.of(0, 5);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<RoomType> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.pageList(anyList(), eq(pg)))
                    .thenReturn(fake);

            Page<RoomType> page = repo.findAll(pg);
            assertSame(fake, page);
        }
    }

    @Test
    void save_insertWhenNull() {
        RoomType rt = new RoomType();
        rt.setTypeId(null);

        RoomType out = repo.save(rt);
        verify(dao).save(rt);
        assertSame(rt, out);
    }

    @Test
    void save_updateWhenPresent() {
        RoomType rt = new RoomType();
        rt.setTypeId(8);

        RoomType out = repo.save(rt);
        verify(dao).update(rt);
        assertSame(rt, out);
    }

    @Test
    void save_exception() {
        RoomType rt = new RoomType();
        doThrow(new RuntimeException("boom"))
                .when(dao).save(any(RoomType.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(rt));
        assertTrue(ex.getMessage().contains("Could not save RoomType"));
    }

    @Test
    void deleteById_existing() {
        RoomType rt = new RoomType();
        when(dao.findById(4)).thenReturn(rt);

        repo.deleteById(4);
        verify(dao).delete(rt);
    }

    @Test
    void deleteById_nonExisting() {
        when(dao.findById(5)).thenReturn(null);
        repo.deleteById(5);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        when(dao.findById(anyInt())).thenReturn(new RoomType());
        doThrow(new RuntimeException("err"))
                .when(dao).delete(any(RoomType.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(6));
        assertTrue(ex.getMessage().contains("Could not delete RoomType"));
    }

    @Test
    void findByName_success() {
        RoomType rt = new RoomType();
        when(jdbc.queryForObject(anyString(), any(), eq("Suite"))).thenReturn(rt);

        Optional<RoomType> opt = repo.findByName("Suite");
        assertTrue(opt.isPresent());
        assertSame(rt, opt.get());
    }

    @Test
    void findByName_exception() {
        when(jdbc.queryForObject(anyString(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByName("X").isEmpty());
    }

    @Test
    void findByNameContaining_delegates() {
        Pageable pg = PageRequest.of(1, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<RoomType> fake = Page.empty();
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

    @Test
    void findByCapacity_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), anyInt()))
                .thenThrow(new RuntimeException());
        Page<RoomType> page = repo.findByCapacity(3, pg);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void findByCapacityGreaterThanEqual_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), anyInt()))
                .thenThrow(new RuntimeException());
        Page<RoomType> page = repo.findByCapacityGreaterThanEqual(7, pg);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }
}
