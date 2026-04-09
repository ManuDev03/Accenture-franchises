package com.jmt.franchises.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductMaxStockResponse {
    private String branchId;
    private String branchName;
    private String productId;
    private String productName;
    private Integer stock;
}
