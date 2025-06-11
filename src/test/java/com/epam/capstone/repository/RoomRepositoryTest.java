package com.epam.capstone.repository;

import com.epam.capstone.model.Room;
import com.epam.capstone.repository.dao.RoomDao;
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
class RoomRepositoryTest {

    @Mock
    private RoomDao dao;

    @Mock
    private CustomJdbcTemplate jdbc;

    private RoomRepository repo;

    @BeforeEach
    void setUp() {
        repo = new RoomRepository(dao, jdbc);
    }

    @Test
    void findById_present() {
        Room r = new Room();
        r.setRoomId(1L);
        when(dao.findById(1L)).thenReturn(r);

        Optional<Room> opt = repo.findById(1L);
        assertTrue(opt.isPresent());
        assertSame(r, opt.get());
    }

    @Test
    void findById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException("err"));
        assertTrue(repo.findById(5L).isEmpty());
    }

    @Test
    void existsById_true() {
        when(dao.findById(2L)).thenReturn(new Room());
        assertTrue(repo.existsById(2L));
    }

    @Test
    void existsById_exception() {
        when(dao.findById(anyLong())).thenThrow(new RuntimeException());
        assertFalse(repo.existsById(3L));
    }

    @Test
    void count_returnsSize() {
        when(dao.findAll()).thenReturn(List.of(new Room(), new Room()));
        assertEquals(2, repo.count());
    }

    @Test
    void count_exception() {
        when(dao.findAll()).thenThrow(new RuntimeException());
        assertEquals(0L, repo.count());
    }

    @Test
    void findAll_pageDelegates() {
        Pageable pg = PageRequest.of(0, 3);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Room> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.pageList(anyList(), eq(pg)))
                    .thenReturn(fake);

            Page<Room> result = repo.findAll(pg);
            assertSame(fake, result);
        }
    }

    @Test
    void save_insertWhenNull() {
        Room r = new Room();
        r.setRoomId(null);

        Room out = repo.save(r);
        verify(dao).save(r);
        assertSame(r, out);
    }

    @Test
    void save_updateWhenPresent() {
        Room r = new Room();
        r.setRoomId(10L);

        Room out = repo.save(r);
        verify(dao).update(r);
        assertSame(r, out);
    }

    @Test
    void save_exception() {
        Room r = new Room();
        doThrow(new RuntimeException("fail"))
                .when(dao).save(any(Room.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.save(r));
        assertTrue(ex.getMessage().contains("Could not save Room"));
    }

    @Test
    void deleteById_existing() {
        Room r = new Room();
        when(dao.findById(5L)).thenReturn(r);

        repo.deleteById(5L);
        verify(dao).delete(r);
    }

    @Test
    void deleteById_nonExisting() {
        when(dao.findById(6L)).thenReturn(null);
        repo.deleteById(6L);
        verify(dao, never()).delete(any());
    }

    @Test
    void deleteById_exception() {
        when(dao.findById(anyLong())).thenReturn(new Room());
        doThrow(new RuntimeException("x"))
                .when(dao).delete(any(Room.class));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> repo.deleteById(7L));
        assertTrue(ex.getMessage().contains("Could not delete Room"));
    }

    @Test
    void findByName_success() {
        Room r = new Room();
        when(jdbc.queryForObject(anyString(), any(), eq("Deluxe"))).thenReturn(r);

        Optional<Room> opt = repo.findByName("Deluxe");
        assertTrue(opt.isPresent());
        assertSame(r, opt.get());
    }

    @Test
    void findByName_exception() {
        when(jdbc.queryForObject(anyString(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(repo.findByName("X").isEmpty());
    }

    @Test
    void findByNameContaining_delegates() {
        Pageable pg = PageRequest.of(0, 4);
        try (MockedStatic<QueryContainsMatcher> qc = mockStatic(QueryContainsMatcher.class)) {
            Page<Room> fake = Page.empty();
            qc.when(() -> QueryContainsMatcher.findByQueryContaining(
                    eq(jdbc), anyString(), anyString(),
                    any(), any(), eq("kw"), eq(pg)
            )).thenReturn(fake);

            assertSame(fake, repo.findByNameContaining("kw", pg));
        }
    }

    @Test
    void findByLocationId_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), anyLong()))
                .thenThrow(new RuntimeException());
        Page<Room> page = repo.findByLocationId(1L, pg);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void findByTypeId_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), anyInt()))
                .thenThrow(new RuntimeException());
        Page<Room> page = repo.findByTypeId(2, pg);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void countByLocationAndType_success() {
        when(jdbc.queryForObject(anyString(), any(), eq(2L), eq(3L))).thenReturn(9L);
        assertEquals(9L, repo.countByLocationAndType(2L, 3L));
    }

    @Test
    void countByLocationAndType_exception() {
        when(jdbc.queryForObject(anyString(), any(), anyLong(), anyLong()))
                .thenThrow(new RuntimeException());
        assertEquals(0L, repo.countByLocationAndType(1L, 1L));
    }

    @Test
    void findByLocationAndTypeAndName_exception() {
        Pageable pg = PageRequest.of(0, 1);
        when(jdbc.queryForObject(anyString(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        Page<Room> page = repo.findByLocationAndTypeAndName(1L, 1, "x", pg);
        assertTrue(page.getContent().isEmpty());
        assertEquals(0, page.getTotalElements());
    }

    @Test
    void findRoomsListByLocationAndType_exception() {
        when(jdbc.query(anyString(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        List<Room> result = repo.findRoomsListByLocationAndType(10L, 20);
        assertTrue(result.isEmpty());
    }
}
