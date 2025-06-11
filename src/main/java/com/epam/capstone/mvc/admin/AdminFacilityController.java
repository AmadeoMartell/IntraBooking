package com.epam.capstone.mvc.admin;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.service.LocationService;
import com.epam.capstone.service.RoomService;
import com.epam.capstone.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/admin/facilities")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminFacilityController {

    private final LocationService locationService;
    private final RoomTypeService roomTypeService;
    private final RoomService roomService;

    @GetMapping
    public String viewFacilities(
            @RequestParam(value = "locPage",  defaultValue = "0") int locPage,
            @RequestParam(value = "locFilter", defaultValue = "") String locFilter,

            @RequestParam(value = "typePage",  defaultValue = "0") int typePage,
            @RequestParam(value = "typeFilter", defaultValue = "") String typeFilter,

            @RequestParam(value = "selectedLocationId", required = false) Long selectedLocationId,

            @RequestParam(value = "roomPage",  defaultValue = "0") int roomPage,
            @RequestParam(value = "roomFilter", defaultValue = "") String roomFilter,

            @RequestParam(value = "selectedTypeId", required = false) Integer selectedTypeId,

            Model model
    ) {

        Page<LocationDto> locPageObj = locationService.findByName(locFilter,
                PageRequest.of(locPage, 5, Sort.by("name")));
        int locTotal = locPageObj.getTotalPages();
        List<Integer> locPages = locTotal > 0
                ? IntStream.range(0, locTotal).boxed().toList()
                : List.of();
        model.addAttribute("locationPage", locPageObj);
        model.addAttribute("locPages", locPages);
        model.addAttribute("locFilter", locFilter);

        model.addAttribute("selectedLocationId", selectedLocationId);

        Page<RoomTypeDto> typePageObj = Page.empty();
        List<Integer> typePages = List.of();
        if (selectedLocationId != null) {
            typePageObj = roomTypeService.findByName(typeFilter,
                    PageRequest.of(typePage, 5, Sort.by("name")));
            int ttot = typePageObj.getTotalPages();
            typePages = ttot > 0
                    ? IntStream.range(0, ttot).boxed().toList()
                    : List.of();
        }
        model.addAttribute("typePage", typePageObj);
        model.addAttribute("typePages", typePages);
        model.addAttribute("typeFilter", typeFilter);
        model.addAttribute("selectedTypeId", selectedTypeId);

        Page<RoomDto> roomPageObj = Page.empty();
        List<Integer> roomPages = List.of();
        if (selectedLocationId != null && selectedTypeId != null) {
            roomPageObj = roomService.findRoomsByLocationAndTypeAndName(
                    selectedLocationId, selectedTypeId,
                    roomFilter, PageRequest.of(roomPage, 5, Sort.by("name")));
            int rtot = roomPageObj.getTotalPages();
            roomPages = rtot > 0
                    ? IntStream.range(0, rtot).boxed().toList()
                    : List.of();
        }
        model.addAttribute("roomPage", roomPageObj);
        model.addAttribute("roomPages", roomPages);
        model.addAttribute("roomFilter", roomFilter);

        return "admin/facilities";
    }
}
