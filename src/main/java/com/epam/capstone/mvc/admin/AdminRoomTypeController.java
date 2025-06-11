package com.epam.capstone.mvc.admin;

import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.model.form.CreateRoomTypeForm;
import com.epam.capstone.service.RoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/roomtypes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomTypeController {

    private final RoomTypeService roomTypeService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createForm", new CreateRoomTypeForm());
        return "admin/roomtype_form";
    }

    @PostMapping("/create")
    public String handleCreateForm(
            @Valid @ModelAttribute("createForm") CreateRoomTypeForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (br.hasErrors()) {
            return "admin/roomtype_form";
        }
        RoomTypeDto dto = new RoomTypeDto(
                null,
                form.getName(),
                form.getCapacity(),
                form.getDescription()
        );
        roomTypeService.createRoomType(dto);
        ra.addFlashAttribute("success", "Room type created successfully");
        return "redirect:/admin/facilities";
    }
}
