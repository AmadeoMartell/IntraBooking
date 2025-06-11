package com.epam.capstone.repository;

import com.epam.capstone.model.Location;
import com.epam.capstone.repository.dao.LocationDao;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationRepositoryTest {

    @Mock
    private LocationDao dao;
    @Mock
    private CustomJdbcTemplate jdbc;

    private LocationRepository repo;

    @BeforeEach
    void setUp() {
        repo = new LocationRepository(dao, jdbc);
    }

    @Test
    void findById_present() {
        Location loc = new Location();
        loc.setLocationId(42L);
        when(dao.findById(42L)).thenReturn(loc);

        Optional<Location> res = repo.findById(42L);
        assertTrue(res.isPresent());
        assertSame(loc, res.get());
    }

    @Test
    void findById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException("oops"));
        Optional<Location> res = repo.findById(7L);
        assertFalse(res.isPresent());
    }

    @Test
    void existsById_true() {
        when(dao.findById(5L)).thenReturn(new Location());
        assertTrue(repo.existsById(5L));
    }

    @Test
    void existsById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById(123L));
    }

    @Test
    void count_returnsSize() {
        List<Location> list = List.of(new Location(), new Location());
        when(dao.findAll()).thenReturn(list);
        assertEquals(2, repo.count());
    }

    @Test
    void count_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void findAll_returnsList() {
        List<Location> list = List.of(new Location());
        when(dao.findAll()).thenReturn(list);
        assertEquals(list, repo.findAll());
    }

    @Test
    void findAll_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertTrue(repo.findAll().isEmpty());
    }

    @Test
    void findAllPage_delegatesToQueryContainsMatcher() {
        var pgReq = PageRequest.of(0, 5);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Location> fakePage = Page.empty();
            qc.when(() -> QueryContainsMatcher.pageList(anyList(), eq(pgReq)))
                    .thenReturn(fakePage);

            Page<Location> p = repo.findAll(pgReq);
            assertSame(fakePage, p);
            qc.verify(() -> QueryContainsMatcher.pageList(anyList(), eq(pgReq)), times(1));
        }
    }

    @Test
    void save_insertsWhenIdNull() {
        Location loc = new Location();
        loc.setLocationId(null);

        Location out = repo.save(loc);
        verify(dao).save(loc);
        assertSame(loc, out);
    }

    @Test
    void save_updatesWhenIdPresent() {
        Location loc = new Location();
        loc.setLocationId(99L);

        Location out = repo.save(loc);
        verify(dao).update(loc);
        assertSame(loc, out);
    }

    @Test
    void deleteById_existing() {
        Location loc = new Location();
        when(dao.findById(7L)).thenReturn(loc);

        repo.deleteById(7L);
        verify(dao).delete(loc);
    }

    @Test
    void deleteById_nonExisting() {
        when(dao.findById(8L)).thenReturn(null);
        // should not throw, and not call delete
        repo.deleteById(8L);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(1L));
        assertTrue(ex.getMessage().contains("Could not delete"));
    }

    @Test
    void findByName_success() {
        Location loc = new Location();
        when(jdbc.queryForObject(anyString(), any(), eq("foo"))).thenReturn(loc);

        Optional<Location> res = repo.findByName("foo");
        assertTrue(res.isPresent());
        assertSame(loc, res.get());
    }

    @Test
    void findByName_exception() {
        when(jdbc.queryForObject(anyString(), any(), any())).thenThrow(new RuntimeException());
        assertTrue(repo.findByName("bar").isEmpty());
    }

    @Test
    void findByNameContaining_delegatesToQueryContainsMatcher() {
        var pg = PageRequest.of(0, 10);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Location> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                            eq(jdbc), anyString(), anyString(), any(), any(), eq("kw"), eq(pg)))
                    .thenReturn(fake);

            Page<Location> p = repo.findByNameContaining("kw", pg);
            assertSame(fake, p);
            qc.verify(() -> QueryContainsMatcher.findByQueryContaining(
                    eq(jdbc), anyString(), anyString(), any(), any(), eq("kw"), eq(pg)), times(1));
        }
    }

    @Test
    void findByAddressContaining_delegatesToQueryContainsMatcher() {
        var pg = PageRequest.of(1, 5);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Location> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                            eq(jdbc), anyString(), anyString(), any(), any(), eq("A"), eq(pg)))
                    .thenReturn(fake);

            assertSame(fake, repo.findByAddressContaining("A", pg));
        }
    }

    @Test
    void findByDescriptionContaining_delegatesToQueryContainsMatcher() {
        var pg = PageRequest.of(2, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Location> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                            eq(jdbc), anyString(), anyString(), any(), any(), eq("D"), eq(pg)))
                    .thenReturn(fake);

            assertSame(fake, repo.findByDescriptionContaining("D", pg));
        }
    }
}
