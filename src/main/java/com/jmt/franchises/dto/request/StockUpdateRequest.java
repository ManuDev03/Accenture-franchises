package com.jmt.franchises.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockUpdateRequest {

    @NotNull(message = "Stock is mandatory")
    @Min(value = 0, message = "Stock can't be negative")
    private Integer stock;
}
