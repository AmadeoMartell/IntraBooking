package com.epam.capstone.repository;

import com.epam.capstone.model.Role;
import com.epam.capstone.repository.dao.RoleDao;
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
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleRepositoryTest {

    @Mock
    private RoleDao dao;

    @Mock
    private CustomJdbcTemplate jdbc;

    private RoleRepository repo;

    @BeforeEach
    void setUp() {
        repo = new RoleRepository(dao, jdbc);
    }

    @Test
    void findById_present() {
        Role r = new Role();
        r.setRoleId(1L);
        when(dao.findById(1L)).thenReturn(r);

        Optional<Role> result = repo.findById(1L);
        assertTrue(result.isPresent());
        assertSame(r, result.get());
    }

    @Test
    void findById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException("boom"));
        Optional<Role> result = repo.findById(5L);
        assertFalse(result.isPresent());
    }

    @Test
    void existsById_true() {
        when(dao.findById(2L)).thenReturn(new Role());
        assertTrue(repo.existsById(2L));
    }

    @Test
    void existsById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById(99L));
    }

    @Test
    void count_returnsSize() {
        List<Role> list = List.of(new Role(), new Role(), new Role());
        when(dao.findAll()).thenReturn(list);
        assertEquals(3L, repo.count());
    }

    @Test
    void count_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void findAll_returnsList() {
        List<Role> list = List.of(new Role());
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
        Pageable pg = PageRequest.of(0, 10);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Role> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.pageList(anyList(), eq(pg)))
                    .thenReturn(fake);

            Page<Role> page = repo.findAll(pg);
            assertSame(fake, page);
            qc.verify(() -> QueryContainsMatcher.pageList(anyList(), eq(pg)), times(1));
        }
    }

    @Test
    void save_insertsWhenIdNull() {
        Role r = new Role();
        r.setRoleId(null);

        Role out = repo.save(r);
        verify(dao).save(r);
        assertSame(r, out);
    }

    @Test
    void save_updatesWhenIdPresent() {
        Role r = new Role();
        r.setRoleId(10L);

        Role out = repo.save(r);
        verify(dao).update(r);
        assertSame(r, out);
    }

    @Test
    void save_exception() {
        Role r = new Role();
        doThrow(new RuntimeException("fail"))
                .when(dao).save(any(Role.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(r));
        assertTrue(ex.getMessage().contains("Could not save Role"));
    }

    @Test
    void deleteById_exceptionOnDelete() {
        Role r = new Role();
        when(dao.findById(1L)).thenReturn(r);
        doThrow(new RuntimeException("oops"))
                .when(dao).delete(r);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(1L));
        assertTrue(ex.getMessage().contains("Could not delete Role"));
    }

    @Test
    void deleteById_existing() {
        Role r = new Role();
        when(dao.findById(7L)).thenReturn(r);

        repo.deleteById(7L);
        verify(dao).delete(r);
    }

    @Test
    void deleteById_nonExisting() {
        when(dao.findById(8L)).thenReturn(null);
        // no exception, no delete call
        repo.deleteById(8L);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException("err"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(1L));
        assertTrue(ex.getMessage().contains("Could not delete Role"));
    }

    @Test
    void findByName_success() {
        Role r = new Role();
        when(jdbc.queryForObject(anyString(), any(), eq("ADMIN"))).thenReturn(r);

        Optional<Role> opt = repo.findByName("ADMIN");
        assertTrue(opt.isPresent());
        assertSame(r, opt.get());
    }

    @Test
    void findByName_exception() {
        when(jdbc.queryForObject(anyString(), any(), any())).thenThrow(new RuntimeException());
        assertTrue(repo.findByName("X").isEmpty());
    }

    @Test
    void findByNameContaining_delegatesToQueryContainsMatcher() {
        Pageable pg = PageRequest.of(1, 5);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Role> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                            eq(jdbc), anyString(), anyString(), any(), any(), eq("kw"), eq(pg)))
                    .thenReturn(fake);

            assertSame(fake, repo.findByNameContaining("kw", pg));
        }
    }

    @Test
    void findByDescriptionContaining_delegatesToQueryContainsMatcher() {
        Pageable pg = PageRequest.of(2, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Role> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                            eq(jdbc), anyString(), anyString(), any(), any(), eq("desc"), eq(pg)))
                    .thenReturn(fake);

            assertSame(fake, repo.findByDescriptionContaining("desc", pg));
        }
    }
}
