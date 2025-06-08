package com.epam.capstone.mvc;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.StatusDto;
import com.epam.capstone.dto.view.BookingView;
import com.epam.capstone.service.BookingService;
import com.epam.capstone.service.LocationService;
import com.epam.capstone.service.RoomService;
import com.epam.capstone.service.StatusService;
import com.epam.capstone.util.PaginationInfo;
import com.epam.capstone.util.PaginationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BookingService bookingService;
    private final RoomService roomService;
    private final LocationService locationService;
    private final StatusService statusService;

    @GetMapping("/")
    public String dashboard(
            Principal principal,
            Model model,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "status", required = false) Short statusId,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir
    ) {
        String username = principal.getName();

        Sort sort = Sort.by("startTime");
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<BookingDto> bookingsPage;
        if (statusId != null) {
            bookingsPage = bookingService.getBookingsByUsernameAndStatus(username, statusId, pageable);
        } else {
            bookingsPage = bookingService.getBookingsByUsername(username, pageable);
        }

        int totalPages = bookingsPage.getTotalPages();
        if (totalPages > 0 && page >= totalPages) {
            String redirectUrl = "/?page=" + (totalPages - 1) + "&size=" + size
                    + (statusId != null ? "&status=" + statusId : "")
                    + "&sortDir=" + sortDir;
            return "redirect:" + redirectUrl;
        }

        PaginationInfo pagination = PaginationUtil.getPaginationInfo(bookingsPage, 3);

        List<BookingView> bookingViews = bookingsPage.getContent().stream()
                .map(booking -> {
                    RoomDto room = roomService.getRoomById(booking.roomId());
                    LocationDto location = locationService.getLocationById(room.locationId());
                    StatusDto status = statusService.getStatusById(booking.statusId());
                    return new BookingView(booking, room, location, status);
                })
                .toList();

        List<StatusDto> allStatuses = statusService.getAllStatuses(Pageable.unpaged()).getContent();

        model.addAttribute("bookingViews",   bookingViews);
        model.addAttribute("currentPage",    pagination.getCurrentPage());
        model.addAttribute("totalPages",     pagination.getTotalPages());
        model.addAttribute("pageSize",       pagination.getPageSize());
        model.addAttribute("startPage",      pagination.getStartPage());
        model.addAttribute("endPage",        pagination.getEndPage());
        model.addAttribute("pageNumbers",    pagination.getPageNumbers());

        model.addAttribute("allStatuses",    allStatuses);
        model.addAttribute("selectedStatus", statusId);
        model.addAttribute("sortDir",        sortDir);

        return "index";
    }
}
