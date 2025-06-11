package com.epam.capstone.model.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Form object for creating a Location.
 */
@Data
public class CreateLocationForm {

    @NotBlank(message = "{createLocationForm.name.required}")
    @Size(max = 100, message = "{createLocationForm.name.size}")
    private String name;

    @Size(max = 255, message = "{createLocationForm.address.size}")
    private String address;

    @Size(max = 255, message = "{createLocationForm.description.size}")
    private String description;
}
