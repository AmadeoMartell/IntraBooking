package com.epam.capstone.service.impl;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.dto.StatusDto;
import com.epam.capstone.dto.mapper.BookingMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Booking;
import com.epam.capstone.model.User;
import com.epam.capstone.repository.BookingRepository;
import com.epam.capstone.repository.UserRepository;
import com.epam.capstone.service.StatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private StatusService statusService;

    @InjectMocks
    private BookingServiceImpl service;

    // A single DTO mock we can reuse in the *map* tests
    private BookingDto dummyDto;

    @BeforeEach
    void setUp() {
        dummyDto = mock(BookingDto.class);
    }

    @Test
    void createBooking_success() {
        BookingDto inputDto = mock(BookingDto.class);
        Booking toSave = new Booking();
        Booking saved = new Booking();
        saved.setBookingId(42L);
        BookingDto outDto = mock(BookingDto.class);

        when(bookingMapper.toEntity(inputDto)).thenReturn(toSave);
        when(bookingRepository.save(toSave)).thenReturn(saved);
        when(bookingMapper.toDto(saved)).thenReturn(outDto);

        BookingDto result = service.createBooking(inputDto);

        assertSame(outDto, result);
        verify(bookingMapper).toEntity(inputDto);
        verify(bookingRepository).save(toSave);
        verify(bookingMapper).toDto(saved);
    }

    @Test
    void getBookingById_found() {
        Booking found = new Booking();
        BookingDto outDto = mock(BookingDto.class);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(found));
        when(bookingMapper.toDto(found)).thenReturn(outDto);

        BookingDto result = service.getBookingById(1L);
        assertSame(outDto, result);
    }

    @Test
    void getBookingById_notFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getBookingById(99L));
    }

    @Test
    void getBookingsByUser_success() {
        Pageable pg = PageRequest.of(0, 2);
        Booking b1 = new Booking(), b2 = new Booking();
        Page<Booking> page = new PageImpl<>(List.of(b1, b2), pg, 2);

        when(bookingRepository.findAllByUserId(7L, pg)).thenReturn(page);
        // map ANY Booking -> the same dummyDto
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(dummyDto);

        Page<BookingDto> result = service.getBookingsByUser(7L, pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(d -> d == dummyDto));
        verify(bookingMapper, times(2)).toDto(any(Booking.class));
    }

    @Test
    void updateBooking_success() {
        BookingDto updateDto = mock(BookingDto.class);
        Booking existing = new Booking();
        existing.setBookingId(5L);
        BookingDto outDto = mock(BookingDto.class);

        when(bookingRepository.findById(5L)).thenReturn(Optional.of(existing));
        // no need to stub partialUpdate — by default it does nothing
        when(bookingMapper.toDto(existing)).thenReturn(outDto);

        BookingDto result = service.updateBooking(5L, updateDto);

        assertSame(outDto, result);
        verify(bookingMapper).partialUpdate(updateDto, existing);
        verify(bookingRepository).save(existing);
    }

    @Test
    void updateBooking_notFound() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateBooking(1L, dummyDto));
    }

    @Test
    void deleteBooking_success() {
        when(bookingRepository.existsById(3L)).thenReturn(true);

        service.deleteBooking(3L);
        verify(bookingRepository).deleteById(3L);
    }

    @Test
    void deleteBooking_notFound() {
        when(bookingRepository.existsById(4L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.deleteBooking(4L));
    }

    @Test
    void createBookingForUser_success() {
        BookingDto dto = mock(BookingDto.class);
        User user = new User();
        user.setUserId(10L);
        Booking toSave = new Booking();
        Booking saved = new Booking();
        saved.setBookingId(11L);
        BookingDto outDto = mock(BookingDto.class);

        when(userRepository.findByUsername("alice"))
                .thenReturn(Optional.of(user));
        when(bookingMapper.toEntity(dto)).thenReturn(toSave);
        // service sets userId on toSave before saving
        when(bookingRepository.save(toSave)).thenAnswer(inv -> {
            assertEquals(10L, toSave.getUserId());
            return saved;
        });
        when(bookingMapper.toDto(saved)).thenReturn(outDto);

        BookingDto result = service.createBookingForUser("alice", dto);
        assertSame(outDto, result);
    }

    @Test
    void createBookingForUser_userNotFound() {
        when(userRepository.findByUsername("bob"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.createBookingForUser("bob", dummyDto));
    }

    @Test
    void getBookingsByUsername_success() {
        User user = new User(); user.setUserId(12L);
        Pageable pg = PageRequest.of(0, 1);
        Booking b = new Booking();
        Page<Booking> page = new PageImpl<>(List.of(b), pg, 1);

        when(userRepository.findByUsername("charlie"))
                .thenReturn(Optional.of(user));
        when(bookingRepository.findAllByUserId(12L, pg)).thenReturn(page);
        when(bookingMapper.toDto(b)).thenReturn(dummyDto);

        Page<BookingDto> result = service.getBookingsByUsername("charlie", pg);
        assertEquals(1, result.getTotalElements());
        assertEquals(List.of(dummyDto), result.getContent());
    }

    @Test
    void getBookingsByUsername_userNotFound() {
        when(userRepository.findByUsername("dennis"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.getBookingsByUsername("dennis", PageRequest.of(0,1)));
    }

    @Test
    void getBookingsByUsernameAndStatus_userNotFound() {
        when(userRepository.findByUsername("frank"))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.getBookingsByUsernameAndStatus(
                        "frank", (short)1, PageRequest.of(0,1)));
    }

    @Test
    void isRoomAvailable_trueAndFalse() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(2);

        when(bookingRepository.countOverlappingBookings(5L, start, end))
                .thenReturn(0L);
        assertTrue(service.isRoomAvailable(5L, start, end));

        when(bookingRepository.countOverlappingBookings(5L, start, end))
                .thenReturn(3L);
        assertFalse(service.isRoomAvailable(5L, start, end));
    }

    @Test
    void getBookingsByStatus() {
        Pageable pg = PageRequest.of(0,1);
        Booking b = new Booking();
        Page<Booking> page = new PageImpl<>(List.of(b), pg, 1);

        when(bookingRepository.findAllByStatusId((short)3, pg))
                .thenReturn(page);
        when(bookingMapper.toDto(b)).thenReturn(dummyDto);

        Page<BookingDto> result = service.getBookingsByStatus((short)3, pg);
        assertEquals(1, result.getTotalElements());
        assertEquals(List.of(dummyDto), result.getContent());
    }

    @Test
    void updateStatuses_success() {
        List<Long> ids = List.of(100L, 101L);
        Booking b1 = new Booking(), b2 = new Booking();
        when(bookingRepository.findById(100L)).thenReturn(Optional.of(b1));
        when(bookingRepository.findById(101L)).thenReturn(Optional.of(b2));

        StatusDto status = mock(StatusDto.class);
        when(status.statusId()).thenReturn((short)7);
        when(statusService.getStatusByName("confirmed")).thenReturn(status);

        service.updateStatuses(ids, "confirmed");

        assertEquals((short)7, b1.getStatusId());
        assertEquals((short)7, b2.getStatusId());

        verify(bookingRepository, times(2)).save(any(Booking.class));

        verifyNoMoreInteractions(bookingRepository);
    }

    @Test
    void updateStatuses_bookingNotFound() {
        when(bookingRepository.findById(200L))
                .thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateStatuses(List.of(200L), "anything"));
    }
}
