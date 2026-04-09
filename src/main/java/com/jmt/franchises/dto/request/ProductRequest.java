package com.jmt.franchises.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductRequest {

    @NotBlank(message = "Product name is mandatory")
    @Size(min = 2, max = 100)
    private String name;

    @NotNull(message = "stock is mandatory")
    @Min(value = 0, message = "Stock can't be negative")
    private Integer stock;
}
