package com.jmt.franchises.mapper;

import com.jmt.franchises.dto.response.BranchResponse;
import com.jmt.franchises.dto.response.FranchiseResponse;
import com.jmt.franchises.dto.response.ProductResponse;
import com.jmt.franchises.model.Branch;
import com.jmt.franchises.model.Franchise;
import com.jmt.franchises.model.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FranchiseMapper {

    FranchiseResponse toResponse(Franchise franchise);

    BranchResponse toResponse(Branch branch);

    ProductResponse toResponse(Product product);
}

