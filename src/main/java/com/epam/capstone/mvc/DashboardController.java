package com.epam.capstone.mvc;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class DashboardController {

    private final BookingService bookingService;

    public DashboardController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/")
    public String dashboard(
            Principal principal,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        String username = principal.getName();

        Pageable pageable = PageRequest.of(page, size);
        Page<BookingDto> bookingsPage = bookingService.getBookingsByUsername(username, pageable);

        int currentPage = bookingsPage.getNumber();
        int totalPages  = bookingsPage.getTotalPages();
        int pageSize    = bookingsPage.getSize();

        if (totalPages > 0 && page >= totalPages) {
            return "redirect:/?page=" + (totalPages - 1) + "&size=" + size;
        }

        int startPage, endPage;
        if (totalPages <= 3) {
            startPage = 0;
            endPage   = totalPages - 1;
        } else {
            if (currentPage == 0) {
                startPage = 0;
                endPage   = 2;
            } else if (currentPage == totalPages - 1) {
                startPage = totalPages - 3;
                endPage   = totalPages - 1;
            } else {
                startPage = currentPage - 1;
                endPage   = currentPage + 1;
            }
        }

        model.addAttribute("bookings",    bookingsPage.getContent());
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages",  totalPages);
        model.addAttribute("pageSize",    pageSize);
        model.addAttribute("startPage",   startPage);
        model.addAttribute("endPage",     endPage);

        return "index";
    }
}
