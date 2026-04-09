package com.jmt.franchises.repository;

import com.jmt.franchises.dto.response.BranchResponse;
import com.jmt.franchises.dto.response.FranchiseResponse;
import com.jmt.franchises.dto.response.ProductResponse;
import com.jmt.franchises.model.Branch;
import com.jmt.franchises.model.Franchise;
import com.jmt.franchises.model.Product;
import org.mapstruct.Mapper;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface FranchiseRepository extends ReactiveMongoRepository<Franchise, String> {

    Mono<Franchise> findByNameIgnoreCase(String nombre);

    Mono<Boolean> existsByNameIgnoreCase(String nombre);
}
