package com.jmt.franchises.service;

import com.jmt.franchises.dto.request.BranchRequest;
import com.jmt.franchises.dto.request.FranchiseRequest;
import com.jmt.franchises.dto.request.ProductRequest;
import com.jmt.franchises.dto.request.StockUpdateRequest;
import com.jmt.franchises.dto.response.BranchResponse;
import com.jmt.franchises.dto.response.FranchiseResponse;
import com.jmt.franchises.dto.response.ProductMaxStockResponse;
import com.jmt.franchises.dto.response.ProductResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseService {

    Mono<FranchiseResponse> createFranchise(FranchiseRequest request);

    Flux<FranchiseResponse> listFranchises();

    Mono<FranchiseResponse> getFranchise(String franchiseId);

    Mono<FranchiseResponse> setFranchiseName(String franchiseId, FranchiseRequest request);

    Mono<BranchResponse> addBranch(String franchiseId, BranchRequest request);

    Mono<BranchResponse> setNameBranch(String franchiseId, String branchId,
                                                    BranchRequest request);

    Mono<ProductResponse> addProduct(String franchiseId, String branchId,
                                          ProductRequest request);

    Mono<Void> deleteProduct(String franchiseId, String branchId, String productId);

    Mono<ProductResponse> updateStock(String franchiseId, String branchId,
                                           String productId, StockUpdateRequest request);

    Mono<ProductResponse> setNameProduct(String franchiseId, String branchId,
                                                    String productId, ProductRequest request);

    Flux<ProductMaxStockResponse> maxStockByBranch(String franchiseId);
}
