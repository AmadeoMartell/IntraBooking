package com.epam.capstone.service.impl;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.dto.mapper.BookingMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Booking;
import com.epam.capstone.model.User;
import com.epam.capstone.repository.BookingRepository;
import com.epam.capstone.repository.UserRepository;
import com.epam.capstone.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link BookingService} that manages roles via BookingRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDto bookingDto) {
        Booking toSave = bookingMapper.toEntity(bookingDto);
        Booking saved = bookingRepository.save(toSave);

        log.info("Created booking with id={}", saved.getBookingId());
        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto getBookingById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found, id=" + bookingId));
        return bookingMapper.toDto(booking);
    }

    @Override
    public Page<BookingDto> getBookingsByUser(Long userId, Pageable pageable) {
        Page<Booking> page = bookingRepository.findAllByUserId(userId, pageable);
        return page.map(bookingMapper::toDto);
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, BookingDto bookingDto) {
        Booking existing = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Cannot update – booking not found, id=" + bookingId));

        bookingMapper.partialUpdate(bookingDto, existing);
        bookingRepository.save(existing);
        log.info("Updated booking id={}", bookingId);
        return bookingMapper.toDto(existing);
    }

    @Override
    @Transactional
    public void deleteBooking(Long bookingId) {
        if (!bookingRepository.existsById(bookingId)) {
            throw new NotFoundException("Cannot delete – booking not found, id=" + bookingId);
        }
        bookingRepository.deleteById(bookingId);
        log.info("Deleted booking id={}", bookingId);
    }

    @Override
    @Transactional
    public BookingDto createBookingForUser(String username, BookingDto bookingDto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        Booking toSave = bookingMapper.toEntity(bookingDto);
        toSave.setUserId(user.getUserId());

        return bookingMapper.toDto(bookingRepository.save(toSave));
    }

    @Override
    public Page<BookingDto> getBookingsByUsername(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));
        return getBookingsByUser(user.getUserId(), pageable);
    }

    @Override
    @Transactional
    public void deleteBookingForUser(String username, Long bookingId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        BookingDto existing = getBookingById(bookingId);
        if (!existing.userId().equals(user.getUserId())) {
            throw new NotFoundException(
                    String.format("Booking %d does not belong to user %s", bookingId, username)
            );
        }

        deleteBooking(bookingId);
    }
}

