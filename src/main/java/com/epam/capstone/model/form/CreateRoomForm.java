package com.epam.capstone.model.form;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateRoomForm {

    @NotNull(message = "{createRoomForm.location.required}")
    private Long locationId;

    @NotNull(message = "{createRoomForm.type.required}")
    private Integer typeId;

    @NotBlank(message = "{createRoomForm.name.required}")
    @Size(max = 100, message = "{createRoomForm.name.size}")
    private String name;

    @NotNull(message = "{createRoomForm.capacity.required}")
    @Min(value = 1, message = "{createRoomForm.capacity.min}")
    private Integer capacity;

    @Size(max = 255, message = "{createRoomForm.description.size}")
    private String description;
}
