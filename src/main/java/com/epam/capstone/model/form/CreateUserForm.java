package com.epam.capstone.model.form;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserForm {
    @NotNull(message = "{createUserForm.roleID.required}")
    private Long roleID;

    @NotBlank(message = "{createUserForm.username.required}")
    @Size(min = 3, max = 50, message = "{createUserForm.username.size}")
    private String username;

    @NotBlank(message = "{createUserForm.password.required}")
    @Size(min = 6, message = "{createUserForm.password.size}")
    private String password;

    @NotBlank(message = "{createUserForm.fullName.required}")
    private String fullName;

    @NotBlank(message = "{createUserForm.email.required}")
    @Email(message = "{createUserForm.email.valid}")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "{createUserForm.phone.pattern}")
    private String phone;
}
