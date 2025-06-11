package com.epam.capstone.mvc.admin;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.model.form.CreateRoomForm;
import com.epam.capstone.model.form.EditRoomForm;
import com.epam.capstone.service.LocationService;
import com.epam.capstone.service.RoomService;
import com.epam.capstone.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final RoomService roomService;
    private final LocationService locationService;
    private final RoomTypeService roomTypeService;

    @GetMapping("/create")
    public String showCreateForm(
            @RequestParam(value = "locationId", required = false) Long locId,
            @RequestParam(value = "typeId", required = false) Integer typeId,
            Model model
    ) {
        CreateRoomForm form = new CreateRoomForm();
        form.setLocationId(locId);
        form.setTypeId(typeId);
        model.addAttribute("createForm", form);

        List<LocationDto> locations = locationService.getAllLocations();
        List<RoomTypeDto> types = roomTypeService.getAllRoomTypes();
        model.addAttribute("locations", locations);
        model.addAttribute("types", types);

        if (locId != null) {
            LocationDto selLoc = locationService.getLocationById(locId);
            model.addAttribute("selectedLocation", selLoc);
        }
        if (typeId != null) {
            RoomTypeDto selType = roomTypeService.getRoomTypeById(typeId);
            model.addAttribute("selectedType", selType);
        }

        return "admin/room_form";
    }

    @PostMapping("/create")
    public String handleCreateForm(
            @Valid @ModelAttribute("createForm") CreateRoomForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        RoomTypeDto rt = roomTypeService.getRoomTypeById(form.getTypeId());

        if (br.hasErrors() || form.getCapacity() < rt.capacity()) {
            if (!br.hasErrors()) {
                br.rejectValue(
                        "capacity",
                        "createRoomForm.capacity.minType",
                        new Object[]{rt.capacity()},
                        null
                );
            }
            model.addAttribute("locations", locationService.getAllLocations());
            model.addAttribute("types", roomTypeService.getAllRoomTypes());
            return "admin/room_form";
        }

        RoomDto dto = new RoomDto(
                null,
                form.getLocationId(),
                form.getTypeId(),
                form.getName(),
                form.getCapacity(),
                form.getDescription()
        );
        roomService.createRoom(dto);

        ra.addFlashAttribute("success", "Room created successfully");
        return "redirect:/admin/facilities?selectedLocationId="
                + form.getLocationId() + "&selectedTypeId=" + form.getTypeId();
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(
            @PathVariable("id") Long roomId,
            Model model
    ) {
        RoomDto room = roomService.getRoomById(roomId);

        EditRoomForm form = new EditRoomForm();
        form.setRoomId(room.roomId());
        form.setLocationId(room.locationId());
        form.setTypeId(room.typeId());
        form.setName(room.name());
        form.setCapacity(room.capacity());
        form.setDescription(room.description());

        model.addAttribute("editForm", form);
        model.addAttribute("locations", locationService.getAllLocations());
        model.addAttribute("types",     roomTypeService.getAllRoomTypes());
        return "admin/room_edit";
    }

    @PutMapping("/edit/{id}")
    public String handleEditForm(
            @PathVariable("id") Long roomId,
            @Valid @ModelAttribute("editForm") EditRoomForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        RoomTypeDto rt = roomTypeService.getRoomTypeById(form.getTypeId());
        if (br.hasErrors() || form.getCapacity() < rt.capacity()) {
            if (!br.hasErrors()) {
                br.rejectValue("capacity",
                        "createRoomForm.capacity.minType",
                        new Object[]{rt.capacity()},
                        null
                );
            }
            model.addAttribute("locations", locationService.getAllLocations());
            model.addAttribute("types",     roomTypeService.getAllRoomTypes());
            return "admin/room_edit";
        }

        RoomDto updated = new RoomDto(
                roomId,
                form.getLocationId(),
                form.getTypeId(),
                form.getName(),
                form.getCapacity(),
                form.getDescription()
        );
        roomService.updateRoom(roomId, updated);

        ra.addFlashAttribute("success", "Room updated successfully");
        return "redirect:/admin/facilities?selectedLocationId="
                + form.getLocationId()
                + "&selectedTypeId=" + form.getTypeId();
    }
}
