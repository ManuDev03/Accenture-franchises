package com.jmt.franchises.controller;

import com.jmt.franchises.dto.request.BranchRequest;
import com.jmt.franchises.dto.request.FranchiseRequest;
import com.jmt.franchises.dto.request.ProductRequest;
import com.jmt.franchises.dto.request.StockUpdateRequest;
import com.jmt.franchises.dto.response.*;
import com.jmt.franchises.service.FranchiseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/franchises")
public class FranchiseController {

    private final FranchiseService franchiseService;

    public FranchiseController(FranchiseService franchiseService) {
        this.franchiseService = franchiseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<FranchiseResponse>> createFranchise(
            @Valid @RequestBody FranchiseRequest request) {

        return franchiseService.createFranchise(request)
                .map(franquicia -> ApiResponse.ok("Franchise created!", franquicia));
    }

    @GetMapping
    public Flux<FranchiseResponse> listFranchises() {
        return franchiseService.listFranchises();
    }

    @GetMapping("/{franchiseId}")
    public Mono<ApiResponse<FranchiseResponse>> getFranchise(
            @PathVariable String franchiseId) {

        return franchiseService.getFranchise(franchiseId)
                .map(f -> ApiResponse.ok("Franchise found", f));
    }

    @PatchMapping("/{franchiseId}/name")
    public Mono<ApiResponse<FranchiseResponse>> updateFranchiseName(
            @PathVariable String franchiseId,
            @Valid @RequestBody FranchiseRequest request) {

        return franchiseService.setFranchiseName(franchiseId, request)
                .map(f -> ApiResponse.ok("Franchise name updated successfully", f));
    }

    @PostMapping("/{franchiseId}/branches")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<BranchResponse>> addBranch(
            @PathVariable String franchiseId,
            @Valid @RequestBody BranchRequest request) {

        return franchiseService.addBranch(franchiseId, request)
                .map(s -> ApiResponse.ok("Branch added successfully", s));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/name")
    public Mono<ApiResponse<BranchResponse>> updateBranchName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody BranchRequest request) {

        return franchiseService.setNameBranch(franchiseId, branchId, request)
                .map(s -> ApiResponse.ok("Branch name updated successfully", s));
    }

    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ApiResponse<ProductResponse>> addProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @Valid @RequestBody ProductRequest request) {

        return franchiseService.addProduct(franchiseId, branchId, request)
                .map(p -> ApiResponse.ok("Product added successfully", p));
    }

    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteProduct(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId) {

        return franchiseService.deleteProduct(franchiseId, branchId, productId);
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public Mono<ApiResponse<ProductResponse>> updateStock(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody StockUpdateRequest request) {

        return franchiseService.updateStock(franchiseId, branchId, productId, request)
                .map(p -> ApiResponse.ok("Stock updated successfully", p));
    }

    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/name")
    public Mono<ApiResponse<ProductResponse>> updateProductName(
            @PathVariable String franchiseId,
            @PathVariable String branchId,
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest request) {

        return franchiseService.setNameProduct(
                        franchiseId, branchId, productId, request)
                .map(p -> ApiResponse.ok("Product name updated successfully", p));
    }

    @GetMapping("/{franchiseId}/products/max-stock")
    public Flux<ProductMaxStockResponse> MaxStockByBranch(
            @PathVariable String franchiseId) {

        return franchiseService.maxStockByBranch(franchiseId);
    }
}
