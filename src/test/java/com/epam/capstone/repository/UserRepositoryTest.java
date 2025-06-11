package com.epam.capstone.repository;

import com.epam.capstone.model.User;
import com.epam.capstone.repository.dao.UserDao;
import com.epam.capstone.repository.dao.rowmapper.UserRowMapper;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryTest {

    @Mock
    private UserDao userDao;

    @Mock
    private CustomJdbcTemplate jdbc;

    private UserRepository repo;

    @BeforeEach
    void setUp() {
        repo = new UserRepository(userDao, jdbc);
    }

    @Test
    void selectByUsernameOrEmail_success() {
        User u = new User();
        when(jdbc.queryForObject(anyString(), any(), eq("alice"), eq("alice@x.com")))
                .thenReturn(u);

        Optional<User> opt = repo.selectByUsernameOrEmail("alice", "alice@x.com");
        assertTrue(opt.isPresent());
        assertSame(u, opt.get());
    }

    @Test
    void selectByUsernameOrEmail_exception() {
        when(jdbc.queryForObject(anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException("fail"));

        assertTrue(repo.selectByUsernameOrEmail("u","e").isEmpty());
    }

    @Test
    void findById_present() {
        User u = new User();
        u.setUserId(7L);
        when(userDao.findById(7L)).thenReturn(u);

        Optional<User> opt = repo.findById(7L);
        assertTrue(opt.isPresent());
        assertSame(u, opt.get());
    }

    @Test
    void findById_exception() {
        when(userDao.findById(anyLong())).thenThrow(new RuntimeException());
        assertTrue(repo.findById(5L).isEmpty());
    }

    @Test
    void existsById_true() {
        when(userDao.findById(3L)).thenReturn(new User());
        assertTrue(repo.existsById(3L));
    }

    @Test
    void existsById_exception() {
        when(userDao.findById(anyLong())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById(4L));
    }

    @Test
    void count_returnsSize() {
        List<User> list = List.of(new User(), new User(), new User());
        when(userDao.findAll()).thenReturn(list);
        assertEquals(3L, repo.count());
    }

    @Test
    void count_exception() {
        when(userDao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void findAll_pageDelegates() {
        Pageable pg = PageRequest.of(0, 5);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<User> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.pageList(anyList(), eq(pg)))
                    .thenReturn(fake);

            Page<User> page = repo.findAll(pg);
            assertSame(fake, page);
        }
    }

    @Test
    void save_insertWhenNull() {
        User u = new User();
        u.setUserId(null);

        User out = repo.save(u);
        verify(userDao).save(u);
        assertSame(u, out);
    }

    @Test
    void save_updateWhenPresent() {
        User u = new User();
        u.setUserId(11L);

        User out = repo.save(u);
        verify(userDao).update(u);
        assertSame(u, out);
    }

    @Test
    void save_exception() {
        User u = new User();
        doThrow(new RuntimeException("boom"))
                .when(userDao).save(any(User.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(u));
        assertTrue(ex.getMessage().contains("Could not save User"));
    }

    @Test
    void deleteById_existing() {
        User u = new User();
        when(userDao.findById(8L)).thenReturn(u);

        repo.deleteById(8L);
        verify(userDao).delete(u);
    }

    @Test
    void deleteById_nonExisting() {
        when(userDao.findById(9L)).thenReturn(null);
        repo.deleteById(9L);
        verify(userDao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        User u = new User();
        when(userDao.findById(anyLong())).thenReturn(u);
        doThrow(new RuntimeException("err"))
                .when(userDao).delete(any(User.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(10L));
        assertTrue(ex.getMessage().contains("Could not delete User"));
    }

    @Test
    void findByUsername_success() {
        User u = new User();
        when(jdbc.queryForObject(anyString(), any(), eq("bob"))).thenReturn(u);

        Optional<User> opt = repo.findByUsername("bob");
        assertTrue(opt.isPresent());
        assertSame(u, opt.get());
    }

    @Test
    void findByUsername_exception() {
        when(jdbc.queryForObject(anyString(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByUsername("x").isEmpty());
    }

    @Test
    void findByEmail_success() {
        User u = new User();
        when(jdbc.queryForObject(anyString(), any(), eq("y@z.com"))).thenReturn(u);

        Optional<User> opt = repo.findByEmail("y@z.com");
        assertTrue(opt.isPresent());
        assertSame(u, opt.get());
    }

    @Test
    void findByEmail_exception() {
        when(jdbc.queryForObject(anyString(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByEmail("e").isEmpty());
    }

    @Test
    void findByFullNameContaining_delegates() {
        Pageable pg = PageRequest.of(1, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<User> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                    eq(jdbc),
                    anyString(),
                    anyString(),
                    any(),
                    any(),
                    eq("kw"),
                    eq(pg)
            )).thenReturn(fake);

            assertSame(fake, repo.findByFullNameContaining("kw", pg));
        }
    }


    @Test
    void findByRoleId_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), anyLong()))
                .thenThrow(new RuntimeException());

        Page<User> page = repo.findByRoleId(7L, pg);
        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }
}
