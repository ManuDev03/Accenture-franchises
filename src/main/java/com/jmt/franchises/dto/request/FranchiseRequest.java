package com.jmt.franchises.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FranchiseRequest {

    @NotBlank(message = "Franchise name empty")
    @Size(min = 2, max = 100, message = "name must have between 2 and 100 characters")
    private String name;

}
