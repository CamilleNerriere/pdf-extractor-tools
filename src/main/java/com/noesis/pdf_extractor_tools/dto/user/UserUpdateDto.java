package com.noesis.pdf_extractor_tools.dto.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {

    private String firstname;

    private String lastname;

    @Size(min = 4, max = 20)
    private String username;
}
