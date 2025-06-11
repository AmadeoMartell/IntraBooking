package com.epam.capstone.repository;

import com.epam.capstone.model.Booking;
import com.epam.capstone.repository.dao.BookingDao;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BookingRepositoryTest {

    @Mock
    private BookingDao bookingDao;

    @Mock
    private CustomJdbcTemplate jdbc;

    @InjectMocks
    private BookingRepository repository;

    private Booking sample;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sample = Booking.builder()
                .bookingId(123L)
                .userId(10L)
                .roomId(20L)
                .statusId((short)1)
                .startTime(LocalDateTime.now().plusDays(1))
                .endTime(LocalDateTime.now().plusDays(2))
                .purpose("test")
                .build();
    }

    @Test
    void findById_returnsBooking() {
        when(bookingDao.findById(123L)).thenReturn(sample);

        Optional<Booking> opt = repository.findById(123L);
        assertThat(opt).contains(sample);

        verify(bookingDao).findById(123L);
    }

    @Test
    void findById_onDaoException_returnsEmpty() {
        when(bookingDao.findById(999L)).thenThrow(new RuntimeException("oops"));

        Optional<Booking> opt = repository.findById(999L);
        assertThat(opt).isEmpty();

        verify(bookingDao).findById(999L);
    }

    @Test
    void existsById_trueAndFalseAndException() {
        when(bookingDao.findById(1L)).thenReturn(sample);
        when(bookingDao.findById(2L)).thenReturn(null);
        when(bookingDao.findById(3L)).thenThrow(new RuntimeException());

        assertThat(repository.existsById(1L)).isTrue();
        assertThat(repository.existsById(2L)).isFalse();
        assertThat(repository.existsById(3L)).isFalse();

        verify(bookingDao, times(3)).findById(anyLong());
    }

    @Test
    void count_returnsSizeOrZero() {
        when(bookingDao.findAll()).thenReturn(List.of(sample, sample));
        assertThat(repository.count()).isEqualTo(2L);

        when(bookingDao.findAll()).thenThrow(new RuntimeException());
        assertThat(repository.count()).isZero();
    }

    @Test
    void findAll_paged() {
        List<Booking> list = List.of(sample);
        Pageable pg = PageRequest.of(0, 10, Sort.by("startTime"));
        when(bookingDao.findAll()).thenReturn(list);

        Page<Booking> page = repository.findAll(pg);
        assertThat(page.getContent()).containsExactly(sample);
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void save_createAndUpdate_andException() {
        Booking newB = Booking.builder().build();
        // create
        repository.save(newB);
        verify(bookingDao).save(newB);

        // update
        newB.setBookingId(5L);
        repository.save(newB);
        verify(bookingDao).update(newB);

        // exception
        doThrow(new RuntimeException("db")).when(bookingDao).save(any());
        Booking fail = Booking.builder().build();
        assertThrows(RuntimeException.class, () -> repository.save(fail));
    }

    @Test
    void deleteById_existingAndNonExistingAndException() {
        when(bookingDao.findById(1L)).thenReturn(sample);
        repository.deleteById(1L);
        verify(bookingDao).delete(sample);

        when(bookingDao.findById(2L)).thenReturn(null);
        repository.deleteById(2L);
        verify(bookingDao, never()).delete(argThat(b -> b.getBookingId().equals(2L)));

        when(bookingDao.findById(3L)).thenThrow(new RuntimeException("nope"));
        assertThrows(RuntimeException.class, () -> repository.deleteById(3L));
    }

    @Test
    void findAllByUserId_ascAndDesc_andException() {
        Pageable ascPg = PageRequest.of(0, 2, Sort.by("startTime").ascending());
        Pageable descPg = PageRequest.of(0, 2, Sort.by("startTime").descending());

        when(jdbc.queryForObject(anyString(), any(), eq(10L))).thenReturn(5L);
        when(jdbc.query(eq(BookingRepository.SELECT_PAGE_BY_USER), any(), eq(10L), anyInt(), anyLong()))
                .thenReturn(List.of(sample));
        Page<Booking> asc = repository.findAllByUserId(10L, ascPg);
        assertThat(asc.getTotalElements()).isEqualTo(5);
        assertThat(asc.getContent()).contains(sample);

        when(jdbc.queryForObject(anyString(), any(), eq(10L))).thenReturn(3L);
        when(jdbc.query(eq(BookingRepository.SELECT_PAGE_BY_USER_DESC), any(), eq(10L), anyInt(), anyLong()))
                .thenReturn(List.of());
        Page<Booking> desc = repository.findAllByUserId(10L, descPg);
        assertThat(desc.getTotalElements()).isEqualTo(3);
        assertThat(desc.getContent()).isEmpty();

        // exception path
        when(jdbc.queryForObject(anyString(), any(), any())).thenThrow(new RuntimeException());
        Page<Booking> empty = repository.findAllByRoomId(99L, ascPg);
        assertThat(empty.getContent()).isEmpty();
        assertThat(empty.getTotalElements()).isZero();
    }

    @Test
    void countOverlappingBookings_successAndException() {
        when(jdbc.queryForObject(eq(BookingRepository.COUNT_OVERLAPPING), any(), eq(20L), any(), any()))
                .thenReturn(7L);
        long cnt = repository.countOverlappingBookings(20L, LocalDateTime.now(), LocalDateTime.now());
        assertThat(cnt).isEqualTo(7);

        when(jdbc.queryForObject(anyString(), any(), anyLong(), any(), any()))
                .thenThrow(new RuntimeException("bad"));
        assertThrows(RuntimeException.class,
                () -> repository.countOverlappingBookings(1L, LocalDateTime.now(), LocalDateTime.now()));
    }
}
