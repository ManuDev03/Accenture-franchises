package com.jmt.franchises.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BranchRequest {

    @NotBlank(message = "Branch name is mandatory")
    @Size(min = 2, max = 100)
    private String name;
}

