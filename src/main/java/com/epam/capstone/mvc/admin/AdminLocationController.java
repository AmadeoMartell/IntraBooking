package com.epam.capstone.mvc.admin;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.model.form.CreateLocationForm;
import com.epam.capstone.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/locations")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminLocationController {

    private final LocationService locationService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createForm", new CreateLocationForm());
        return "admin/location_form";
    }

    @PostMapping("/create")
    public String handleCreateForm(
            @Valid @ModelAttribute("createForm") CreateLocationForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (br.hasErrors()) {
            return "admin/location_form";
        }
        LocationDto dto = new LocationDto(
                null,
                form.getName(),
                form.getAddress(),
                form.getDescription()
        );
        locationService.createLocation(dto);
        ra.addFlashAttribute("success", "Location created successfully");
        return "redirect:/admin/facilities";
    }
}
