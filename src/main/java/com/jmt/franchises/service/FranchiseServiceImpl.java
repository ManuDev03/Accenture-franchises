package com.jmt.franchises.service;

import com.jmt.franchises.dto.request.BranchRequest;
import com.jmt.franchises.dto.request.FranchiseRequest;
import com.jmt.franchises.dto.request.ProductRequest;
import com.jmt.franchises.dto.request.StockUpdateRequest;
import com.jmt.franchises.dto.response.BranchResponse;
import com.jmt.franchises.dto.response.FranchiseResponse;
import com.jmt.franchises.dto.response.ProductMaxStockResponse;
import com.jmt.franchises.dto.response.ProductResponse;
import com.jmt.franchises.exception.BusinessException;
import com.jmt.franchises.exception.DuplicateResourceException;
import com.jmt.franchises.exception.ResourceNotFoundException;
import com.jmt.franchises.mapper.FranchiseMapper;
import com.jmt.franchises.model.Branch;
import com.jmt.franchises.model.Franchise;
import com.jmt.franchises.model.Product;
import com.jmt.franchises.repository.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@Service
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final FranchiseMapper franchiseMapper;

    public FranchiseServiceImpl(FranchiseRepository franchiseRepository,FranchiseMapper franchiseMapper) {
        this.franchiseRepository = franchiseRepository;
        this.franchiseMapper = franchiseMapper;
    }


    @Override
    public Mono<FranchiseResponse> createFranchise(FranchiseRequest request) {
        return franchiseRepository.existsByNameIgnoreCase(request.getName())
                .flatMap(exists -> {
                    if (exists)
                        return Mono.error(new DuplicateResourceException("Franchise", "name", request.getName()));

                    Franchise franchise = Franchise.builder().name(request.getName()).build();

                    return franchiseRepository.save(franchise);
                })
                .map(franchiseMapper::toResponse);
    }

    @Override
    public Flux<FranchiseResponse> listFranchises() {
        return franchiseRepository.findAll().map(franchiseMapper::toResponse);
    }

    @Override
    public Mono<FranchiseResponse> getFranchise(String franchiseId) {
        return franchiseRepository.findById(franchiseId).switchIfEmpty(
                        Mono.error(
                                new ResourceNotFoundException("Franquicia", "id", franchiseId)))
                .map(franchiseMapper::toResponse);
    }

    @Override
    public Mono<FranchiseResponse> setFranchiseName(String franchiseId, FranchiseRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise", "id", franchiseId)))
                .flatMap(franchise -> {

                    return franchiseRepository.existsByNameIgnoreCase(request.getName())
                            .flatMap(exists -> {
                                if (exists)
                                    return Mono.error(new DuplicateResourceException("Franquicia", "nombre", request.getName()));
                                franchise.setName(request.getName());
                                return franchiseRepository.save(franchise);
                            }).map(franchiseMapper::toResponse);
                });
    }

    @Override
    public Mono<BranchResponse> addBranch(String franchiseId, BranchRequest request) {
        return franchiseRepository.findById(franchiseId).flatMap(franchise -> {
            if (franchise.getBranches().stream().anyMatch(branch -> branch.getName().equalsIgnoreCase(request.getName())))
                return Mono.error(new BusinessException(
                        "It already exists a branch with name '" + request.getName()
                                + "' in this franchise"));

            Branch newBranch = Branch.builder().name(request.getName()).build();

            franchise.getBranches().add(newBranch);

            return franchiseRepository.save(franchise).map(f -> newBranch)
                    .map(franchiseMapper::toResponse);
        });
    }

    @Override
    public Mono<BranchResponse> setNameBranch(String franchiseId, String branchId, BranchRequest request) {
        return franchiseRepository.findById(franchiseId).flatMap(franchise -> {
            Branch branch = searchBranch(franchise, branchId);

            boolean duplicatedName = franchise.getBranches().stream()
                    .filter(s -> !s.getId().equals(branchId))
                    .anyMatch(s -> s.getName().equalsIgnoreCase(request.getName()));

            if (duplicatedName)
                return Mono.error(new BusinessException("It already exists a branch with name '" + request.getName()
                        + "' in this franchise"));

            branch.setName(request.getName());

            return franchiseRepository.save(franchise)
                    .map(f -> branch);
        }).map(franchiseMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> addProduct(String franchiseId, String branchId, ProductRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise", "id", franchiseId)))
                .flatMap(franchise -> {
                    Branch branch = searchBranch(franchise, branchId);

                    boolean duplicatedName = branch.getProducts().stream()
                            .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));

                    if (duplicatedName) {
                        return Mono.error(new BusinessException(
                                "It already exists a product with name '" + request.getName()
                                        + "' in this branch"));
                    }

                    Product newProduct = Product.builder()
                            .name(request.getName().trim())
                            .stock(request.getStock())
                            .build();

                    branch.getProducts().add(newProduct);

                    return franchiseRepository.save(franchise)
                            .map(f -> newProduct);
                })
                .map(franchiseMapper::toResponse);
    }

    @Override
    public Mono<Void> deleteProduct(String franchiseId, String branchId, String productId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise", "id", franchiseId)))
                .flatMap(franchise -> {
                    Branch sucursal = searchBranch(franchise, branchId);

                    boolean existeProducto = sucursal.getProducts().stream()
                            .anyMatch(p -> p.getId().equals(productId));

                    if (!existeProducto) {
                        return Mono.error(new ResourceNotFoundException(
                                "Product", "id", productId));
                    }

                    sucursal.getProducts().removeIf(p -> p.getId().equals(productId));

                    return franchiseRepository.save(franchise);
                })
                .then();
    }

    @Override
    public Mono<ProductResponse> updateStock(String franchiseId, String branchId, String productId, StockUpdateRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise", "id", franchiseId)))
                .flatMap(franquicia -> {
                    Branch branch = searchBranch(franquicia, branchId);
                    Product producto = searchProduct(branch, productId);

                    producto.setStock(request.getStock());

                    return franchiseRepository.save(franquicia)
                            .map(f -> producto);
                })
                .map(franchiseMapper::toResponse);
    }

    @Override
    public Mono<ProductResponse> setNameProduct(String franchiseId, String branchId, String productId, ProductRequest request) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franchise", "id", franchiseId)))
                .flatMap(franquicia -> {
                    Branch branch = searchBranch(franquicia, branchId);
                    Product product = searchProduct(branch, productId);

                    boolean nombreDuplicado = branch.getProducts().stream()
                            .filter(p -> !p.getId().equals(productId))
                            .anyMatch(p -> p.getName().equalsIgnoreCase(request.getName()));

                    if (nombreDuplicado) {
                        return Mono.error(new BusinessException(
                                "It already exists a product with name '" + request.getName()
                                        + "' in this branch"));
                    }

                    product.setName(request.getName().trim());
                    product.setStock(request.getStock());

                    return franchiseRepository.save(franquicia)
                            .map(f -> product);
                })
                .map(franchiseMapper::toResponse);
    }

    @Override
    public Flux<ProductMaxStockResponse> maxStockByBranch(String franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(
                        new ResourceNotFoundException("Franquicia", "id", franchiseId)))
                .flatMapMany(franchise ->
                        Flux.fromIterable(franchise.getBranches())
                                .filter(sucursal -> !sucursal.getProducts().isEmpty())
                                .map(sucursal -> {
                                    Product productMaxStock = sucursal.getProducts()
                                            .stream()
                                            .max(Comparator.comparingInt(Product::getStock))
                                            .orElseThrow();

                                    return ProductMaxStockResponse.builder()
                                            .branchId(sucursal.getId())
                                            .branchName(sucursal.getName())
                                            .productId(productMaxStock.getId())
                                            .productName(productMaxStock.getName())
                                            .stock(productMaxStock.getStock())
                                            .build();
                                })
                );
    }

    private Branch searchBranch(Franchise franchise, String branchId) {
        return franchise.getBranches().stream().filter(b -> b.getId().equals(branchId)).findFirst().orElseThrow(() -> new ResourceNotFoundException("Branch", "id", branchId));
    }

    private Product searchProduct(Branch branch, String productId) {
        return branch.getProducts().stream()
                .filter(p -> p.getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }
}
