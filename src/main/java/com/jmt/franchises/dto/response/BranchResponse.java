package com.jmt.franchises.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BranchResponse {
    private String id;
    private String name;
    private List<ProductResponse> products;
}
