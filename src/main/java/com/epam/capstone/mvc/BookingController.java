package com.epam.capstone.mvc;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.model.form.BookingForm;
import com.epam.capstone.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/booking")
@SessionAttributes("bookingForm")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final LocationService locationService;
    private final RoomTypeService roomTypeService;
    private final RoomService roomService;
    private final StatusService statusService;


    @ModelAttribute("bookingForm")
    public BookingForm createBookingForm() {
        return new BookingForm();
    }

    @GetMapping
    public String bookingRoot() {
        return "redirect:/booking/step1";
    }

    @GetMapping("/step1")
    @PreAuthorize("isAuthenticated()")
    public String showStep1(Model model) {
        model.addAttribute("locations", locationService.getAllLocations());
        return "booking/select_location";
    }

    @PostMapping("/step1")
    @PreAuthorize("isAuthenticated()")
    public String processStep1(
            @ModelAttribute("bookingForm")
            @Validated(BookingForm.Step1.class) BookingForm bookingForm,
            BindingResult br,
            Model model
    ) {
        if (br.hasErrors()) {
            model.addAttribute("locations", locationService.getAllLocations());
            return "booking/select_location";
        }
        return "redirect:/booking/step2";
    }

    /**
     * GET /booking/step2
     *   • If no locationId → redirect back to step1.
     *   • If bookingForm.roomTypeId != null, also load a Page<RoomDto> by calling findRoomsByLocationAndTypeAndName(...)
     */
    @GetMapping("/step2")
    @PreAuthorize("isAuthenticated()")
    public String showStep2(
            @ModelAttribute("bookingForm") BookingForm bookingForm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "name", required = false) String nameFilter,
            Model model
    ) {
        String redirect = prepareStep2Model(bookingForm, model);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("nameFilter", nameFilter);

        Integer typeId = bookingForm.getRoomTypeId();
        if (typeId != null) {
            Long locId = bookingForm.getLocationId();
            Pageable pageable = PageRequest.of(page, size);

            Page<RoomDto> roomsPage = roomService
                    .findRoomsByLocationAndTypeAndName(locId, typeId, nameFilter, pageable);

            model.addAttribute("roomsPage", roomsPage);
            model.addAttribute("currentPage", roomsPage.getNumber());
            model.addAttribute("totalPages", roomsPage.getTotalPages());
            model.addAttribute("pageSize", roomsPage.getSize());

            if (roomsPage.getTotalPages() > 0) {
                List<Integer> pageNumbers = IntStream
                        .range(0, roomsPage.getTotalPages())
                        .boxed()
                        .toList();
                model.addAttribute("pageNumbers", pageNumbers);
            }
        }

        return "booking/select_type_and_room";
    }

    @PostMapping(
            value  = "/step2",
            params = "action=prev"
    )
    @PreAuthorize("isAuthenticated()")
    public String handleStep2Prev(
            @ModelAttribute("bookingForm") BookingForm bookingForm
    ) {
        return "redirect:/booking/step1";
    }

    /**
     * POST /booking/step2?action=selectType
     *   • Binds only Step2Type (roomTypeId).
     *   • If roomTypeId is null → validation error.
     *   • Otherwise, fetch page=0 of rooms by calling findRoomsByLocationAndTypeAndName(..., nameFilter, ...)
     */
    @PostMapping(
            value  = "/step2",
            params = "action=selectType"
    )
    @PreAuthorize("isAuthenticated()")
    public String handleStep2SelectType(
            @ModelAttribute("bookingForm")
            @Validated(BookingForm.Step2Type.class) BookingForm bookingForm,
            BindingResult br,
            @RequestParam(value = "name", required = false) String nameFilter,
            Model model
    ) {
        String redirect = prepareStep2Model(bookingForm, model);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("nameFilter", nameFilter);

        if (bookingForm.getRoomTypeId() == null) {
            br.rejectValue("roomTypeId", "booking.step2.errorType", "Please select a room type");
        }
        if (br.hasErrors()) {
            return "booking/select_type_and_room";
        }

        Long locId = bookingForm.getLocationId();
        Integer typeId = bookingForm.getRoomTypeId();
        int page = 0;
        int size = 5;
        Pageable pageable = PageRequest.of(page, size);

        Page<RoomDto> roomsPage = roomService
                .findRoomsByLocationAndTypeAndName(locId, typeId, nameFilter, pageable);

        model.addAttribute("roomsPage", roomsPage);
        model.addAttribute("currentPage", roomsPage.getNumber());
        model.addAttribute("totalPages", roomsPage.getTotalPages());
        model.addAttribute("pageSize", roomsPage.getSize());

        if (roomsPage.getTotalPages() > 0) {
            List<Integer> pageNumbers = IntStream
                    .range(0, roomsPage.getTotalPages())
                    .boxed()
                    .toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "booking/select_type_and_room";
    }

    /**
     * POST /booking/step2?action=next
     *   • Binds both Step2Type and Step2Room (roomTypeId + roomId).
     *   • If either is missing → validation error and re‐render current page‐with‐pagination.
     *   • Otherwise, redirect to step3.
     */
    @PostMapping(
            value  = "/step2",
            params = "action=next"
    )
    @PreAuthorize("isAuthenticated()")
    public String handleStep2Next(
            @ModelAttribute("bookingForm")
            @Validated({BookingForm.Step2Type.class, BookingForm.Step2Room.class})
            BookingForm bookingForm,
            BindingResult br,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            @RequestParam(value = "name", required = false) String nameFilter,
            Model model
    ) {
        String redirect = prepareStep2Model(bookingForm, model);
        if (redirect != null) {
            return redirect;
        }

        model.addAttribute("nameFilter", nameFilter);

        if (bookingForm.getRoomTypeId() == null) {
            br.rejectValue("roomTypeId", "booking.step2.errorType", "Please select a room type");
        }
        if (bookingForm.getRoomId() == null) {
            br.rejectValue("roomId", "booking.step2.errorRoom", "Please select a room");
        }

        if (br.hasErrors()) {
            if (bookingForm.getRoomTypeId() != null) {
                Long locId = bookingForm.getLocationId();
                Integer typeId = bookingForm.getRoomTypeId();
                Pageable pageable = PageRequest.of(page, size);

                Page<RoomDto> roomsPage = roomService
                        .findRoomsByLocationAndTypeAndName(locId, typeId, nameFilter, pageable);

                model.addAttribute("roomsPage", roomsPage);
                model.addAttribute("currentPage", roomsPage.getNumber());
                model.addAttribute("totalPages", roomsPage.getTotalPages());
                model.addAttribute("pageSize", roomsPage.getSize());

                if (roomsPage.getTotalPages() > 0) {
                    List<Integer> pageNumbers = IntStream
                            .range(0, roomsPage.getTotalPages())
                            .boxed()
                            .toList();
                    model.addAttribute("pageNumbers", pageNumbers);
                }
            }
            return "booking/select_type_and_room";
        }

        return "redirect:/booking/step3";
    }

    /**
     * Shared helper:
     *  • If bookingForm.locationId == null → redirect to step1.
     *  • Otherwise, load locationName + roomTypes + possibly “noTypes”.
     */
    private String prepareStep2Model(BookingForm bookingForm, Model model) {
        Long locId = bookingForm.getLocationId();
        if (locId == null) {
            return "redirect:/booking/step1";
        }

        LocationDto loc = locationService.getLocationById(locId);
        model.addAttribute("locationName", loc.name());

        List<RoomTypeDto> types = roomTypeService.getAvailableRoomTypesForLocation(locId);
        model.addAttribute("roomTypes", types);

        if (types.isEmpty()) {
            model.addAttribute("noTypes", true);
        }

        return null;
    }

    /**
     * GET /booking/step3
     *  – Requires that locationId, roomTypeId and roomId are already in the form.
     *  – Calculates min/max allowed bounds for the date pickers:
     *        min = tomorrow at 00:00
     *        max = today + 2 weeks at 23:59
     */
    @GetMapping("/step3")
    @PreAuthorize("isAuthenticated()")
    public String showStep3(
            @ModelAttribute("bookingForm") BookingForm bookingForm,
            Model model
    ) {
        if (bookingForm.getLocationId() == null
                || bookingForm.getRoomTypeId() == null
                || bookingForm.getRoomId() == null) {
            return "redirect:/booking/step2";
        }

        LocalDate today      = LocalDate.now();
        LocalDateTime min    = today.plusDays(1).atStartOfDay();
        LocalDateTime max    = today.plusWeeks(2).atTime(LocalTime.MAX);

        model.addAttribute("minDateTime", min);
        model.addAttribute("maxDateTime", max);

        return "booking/enter_purpose_and_time";
    }

    /**
     * POST /booking/step3
     *  – Binds the purpose, startDateTime & endDateTime (Step3 group).
     *  – Validates:
     *       • fields not null/blank
     *       • start < end
     *       • both within [min … max]
     *       • room availability
     */
    @PostMapping("/step3")
    @PreAuthorize("isAuthenticated()")
    public String processStep3(
            @ModelAttribute("bookingForm")
            @Validated(BookingForm.Step3.class) BookingForm bookingForm,
            BindingResult br,
            Model model,
            Principal principal
    ) {
        LocalDate today      = LocalDate.now();
        LocalDateTime min    = today.plusDays(1).atStartOfDay();
        LocalDateTime max    = today.plusWeeks(2).atTime(LocalTime.MAX);
        model.addAttribute("minDateTime", min);
        model.addAttribute("maxDateTime", max);

        LocalDateTime start = bookingForm.getStartDateTime();
        LocalDateTime end   = bookingForm.getEndDateTime();

        if (!br.hasFieldErrors("startDateTime")
                && !br.hasFieldErrors("endDateTime")) {

            if (start.isAfter(end) || start.isEqual(end)) {
                br.rejectValue("endDateTime",
                        "booking.step3.errorOrder",
                        "End time must be after start time");
            }

            if (start.isBefore(min) || end.isAfter(max)) {
                br.reject("booking.step3.errorWindow",
                        new Object[]{min.toLocalDate(), max.toLocalDate()},
                        "Booking must be between " +
                                min.toLocalDate() + " and " +
                                max.toLocalDate()
                );
            }
            if (!br.hasErrors() && !start.toLocalDate().equals(end.toLocalDate())) {
                br.reject("booking.step3.errorSameDay",
                        "Start and end must be on the same day");
            }

            if (!br.hasErrors()) {
                boolean free = bookingService.isRoomAvailable(
                        bookingForm.getRoomId(), start, end
                );
                if (!free) {
                    br.reject("booking.step3.errorConflict",
                            "This room is already booked in that time range");
                }
            }
        }

        if (br.hasErrors()) {
            return "booking/enter_purpose_and_time";
        }

        var statusDto = statusService.getStatusByName("PENDING");
        var bookingDto = bookingForm.toBookingDto(statusDto.statusId());
        bookingService.createBookingForUser(principal.getName(), bookingDto);
        return "redirect:/booking/confirmation";
    }

    @GetMapping("/confirmation")
    @PreAuthorize("isAuthenticated()")
    public String showConfirmation(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "booking/confirmation";
    }
}
