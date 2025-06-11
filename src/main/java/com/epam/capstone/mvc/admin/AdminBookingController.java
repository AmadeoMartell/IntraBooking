package com.epam.capstone.mvc.admin;

import com.epam.capstone.dto.*;
import com.epam.capstone.dto.view.BookingFullView;
import com.epam.capstone.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/bookings")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBookingController {

    private final BookingService bookingService;
    private final StatusService statusService;
    private final RoomService roomService;
    private final LocationService locationService;
    private final UserService userService;

    @GetMapping
    public String listBookings(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "status", defaultValue = "PENDING", required = false) String status,
            @RequestParam(value = "sortDir", defaultValue = "asc") String sortDir,
            Model model
    ) {
        Sort sort = Sort.by("startTime");
        sort = "desc".equalsIgnoreCase(sortDir) ? sort.descending() : sort.ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        var statusDto = statusService.getStatusByName(status);
        Page<BookingDto> bookings = bookingService.getBookingsByStatus(statusDto.statusId(), pageable);
        Page<BookingFullView> pageViews = bookings.map(
                booking -> {
                    RoomDto room = roomService.getRoomById(booking.roomId());
                    LocationDto location = locationService.getLocationById(room.locationId());
                    UserDto user = userService.getUserById(booking.userId());
                    return new BookingFullView(booking, user, room, location, statusDto);
                });

        int totalPages = pageViews.getTotalPages();
        List<Integer> pageNumbers = totalPages > 0
                ? IntStream.range(0, totalPages).boxed().toList()
                : List.of();

        List<StatusDto> allStatuses = statusService.findAll();

        model.addAttribute("bookingViews", pageViews.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageNumbers", pageNumbers);

        model.addAttribute("selectedStatus", status);
        model.addAttribute("allStatuses", allStatuses);
        model.addAttribute("sortDir", sortDir);

        return "admin/bookings_list";
    }

    @PostMapping("/status")
    public String bulkUpdateStatus(
            @RequestParam("bookingIds") List<Long> bookingIds,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes ra
    ) {
        bookingService.updateStatuses(bookingIds, newStatus);
        ra.addFlashAttribute("success", "Changed status for " + bookingIds.size() + " booking(s)");
        return "redirect:/admin/bookings";
    }
}
