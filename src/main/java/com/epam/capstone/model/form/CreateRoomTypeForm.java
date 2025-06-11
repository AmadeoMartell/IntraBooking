package com.epam.capstone.model.form;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * Form object for creating a Room Type.
 */
@Data
public class CreateRoomTypeForm {

    @NotBlank(message = "{createRoomTypeForm.name.required}")
    @Size(max = 50, message = "{createRoomTypeForm.name.size}")
    private String name;

    @NotNull(message = "{createRoomTypeForm.capacity.required}")
    @Min(value = 1, message = "{createRoomTypeForm.capacity.min}")
    private Integer capacity;

    @Size(max = 255, message = "{createRoomTypeForm.description.size}")
    private String description;
}
