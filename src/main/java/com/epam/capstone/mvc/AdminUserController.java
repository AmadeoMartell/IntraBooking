package com.epam.capstone.mvc;

import com.epam.capstone.dto.UserDto;
import com.epam.capstone.exception.AlreadyExistException;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.form.CreateUserForm;
import com.epam.capstone.model.form.EditUserForm;
import com.epam.capstone.service.RoleService;
import com.epam.capstone.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String listUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "name", defaultValue = "", required = false) String nameFilter,
            Model model
    ) {
        Page<UserDto> usersPage = userService.findUsersByName(nameFilter, PageRequest.of(page, size));
        int totalPages = usersPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.range(0, totalPages).boxed().toList();
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("nameFilter", nameFilter);
        return "admin/user_list";
    }

    @PostMapping("/delete")
    public String deleteUsers(
            @RequestParam("selectedIds") List<Long> ids,
            RedirectAttributes ra
    ) {
        userService.deleteUsers(ids);
        ra.addFlashAttribute("success", "Deleted " + ids.size() + " user(s).");
        return "redirect:/admin/users";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("createForm", new CreateUserForm());
        model.addAttribute("roles", roleService.findAll());
        return "admin/user_form";
    }

    @PostMapping("/create")
    public String handleCreateForm(
            @Valid @ModelAttribute("createForm") CreateUserForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (br.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "admin/user_form";
        }

        UserDto dto = new UserDto(
                null,
                form.getRoleID(),
                form.getUsername(),
                passwordEncoder.encode(form.getPassword()),
                form.getFullName(),
                form.getEmail(),
                form.getPhone(),
                null,
                null
        );
        userService.createUser(dto);

        ra.addFlashAttribute("success", "User created successfully");
        return "redirect:/admin/users";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        UserDto user = userService.getUserById(id);
        EditUserForm editForm = new EditUserForm();
        editForm.setUserId(user.userId());
        editForm.setRoleID(user.roleId());
        editForm.setUsername(user.username());
        editForm.setFullName(user.fullName());
        editForm.setEmail(user.email());
        editForm.setPhone(user.phone());

        model.addAttribute("editForm", editForm);
        model.addAttribute("roles", roleService.findAll());
        return "admin/user_edit_form";
    }

    @PostMapping("/{id}/edit")
    public String handleEditForm(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("editForm") EditUserForm form,
            BindingResult br,
            Model model,
            RedirectAttributes ra
    ) {
        if (!form.isPasswordValid()) {
            br.rejectValue("password", "{createUserForm.password.size}", "Password must be between 6 and 100 characters");
        }
        if (br.hasErrors()) {
            model.addAttribute("roles", roleService.findAll());
            return "admin/user_edit_form";
        }

        UserDto existingUser = userService.getUserById(id);
        String password = existingUser.passwordHash();

        if (form.getPassword() != null && !form.getPassword().trim().isEmpty()) {
            password = passwordEncoder.encode(form.getPassword());
        }

        UserDto dto = new UserDto(
                form.getUserId(),
                form.getRoleID(),
                form.getUsername(),
                password,
                form.getFullName(),
                form.getEmail(),
                form.getPhone(),
                existingUser.createdAt(),
                LocalDateTime.now()
        );

        userService.updateUser(id, dto);
        ra.addFlashAttribute("success", "User updated successfully");
        return "redirect:/admin/users";
    }

    @ExceptionHandler(AlreadyExistException.class)
    public String handleAlreadyExist(
            AlreadyExistException ex,
            HttpServletRequest request,
            Model model
    ) {
        CreateUserForm form = new CreateUserForm();
        try {
            form.setRoleID(Long.valueOf(request.getParameter("roleID")));
        } catch (NumberFormatException ignored) { }
        form.setUsername(request.getParameter("username"));
        form.setPassword(request.getParameter("password"));
        form.setFullName(request.getParameter("fullName"));
        form.setEmail(request.getParameter("email"));
        form.setPhone(request.getParameter("phone"));

        model.addAttribute("createForm", form);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("error", ex.getMessage());
        return "admin/user_form";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleAlreadyExist(
            NotFoundException ex,
            HttpServletRequest request,
            Model model
    ) {
        EditUserForm form = new EditUserForm();
        try {
            form.setUserId(Long.valueOf(request.getParameter("userId")));
            form.setRoleID(Long.valueOf(request.getParameter("roleID")));
        } catch (NumberFormatException ignored) { }
        form.setUsername(request.getParameter("username"));
        form.setPassword(request.getParameter("password"));
        form.setFullName(request.getParameter("fullName"));
        form.setEmail(request.getParameter("email"));
        form.setPhone(request.getParameter("phone"));

        model.addAttribute("createForm", form);
        model.addAttribute("roles", roleService.findAll());
        model.addAttribute("error", ex.getMessage());
        return "admin/user_form";
    }
}
