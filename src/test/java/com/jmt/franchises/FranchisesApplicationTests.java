package com.jmt.franchises;

import com.jmt.franchises.dto.request.BranchRequest;
import com.jmt.franchises.dto.request.FranchiseRequest;
import com.jmt.franchises.dto.response.BranchResponse;
import com.jmt.franchises.dto.response.FranchiseResponse;
import com.jmt.franchises.dto.response.ProductResponse;
import com.jmt.franchises.exception.BusinessException;
import com.jmt.franchises.exception.DuplicateResourceException;
import com.jmt.franchises.exception.ResourceNotFoundException;
import com.jmt.franchises.mapper.FranchiseMapper;
import com.jmt.franchises.model.Branch;
import com.jmt.franchises.model.Franchise;
import com.jmt.franchises.model.Product;
import com.jmt.franchises.repository.FranchiseRepository;
import com.jmt.franchises.service.FranchiseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FranchiseServiceImpl - Pruebas Unitarias")
class FranchisesApplicationTests {

    @Test
    void contextLoads() {
    }

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private FranchiseMapper franchiseMapper;

    @InjectMocks
    private FranchiseServiceImpl franchiseService;

    private Product product;
    private Branch branch;
    private Franchise franchise;

    private ProductResponse productResponse;
    private BranchResponse branchResponse;
    private FranchiseResponse franchiseResponse;


    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id("product-id-1")
                .name("Big Mac")
                .stock(100)
                .build();

        branch = Branch.builder()
                .id("branch-id-1")
                .name("Chapinero Branch")
                .products(new ArrayList<>(List.of(product)))
                .build();

        franchise = Franchise.builder()
                .id("franchise-id-1")
                .name("McDonald's")
                .branches(new ArrayList<>(List.of(branch)))
                .build();

        productResponse = ProductResponse.builder()
                .id("product-id-1")
                .name("Big Mac")
                .stock(100)
                .build();

        branchResponse = BranchResponse.builder()
                .id("branch-id-1")
                .name("Chapinero Branch")
                .build();

        franchiseResponse = FranchiseResponse.builder()
                .id("franchise-id-1")
                .name("McDonald's")
                .build();
    }

    @Nested
    @DisplayName("createFranchise()")
    class CreateFranchise {

        @Test
        @DisplayName("Debe crear franquicia exitosamente cuando el nombre no existe")
        void createFranchise_success() {
            FranchiseRequest request = new FranchiseRequest();
            request.setName("Burger King");

            when(franchiseRepository.existsByNameIgnoreCase("Burger King"))
                    .thenReturn(Mono.just(false));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(franchise));
            when(franchiseMapper.toResponse(franchise))
                    .thenReturn(franchiseResponse);

            StepVerifier.create(franchiseService.createFranchise(request))
                    .expectNextMatches(response ->
                            response.getId().equals("franchise-id-1") &&
                                    response.getName().equals("McDonald's"))
                    .verifyComplete();

            verify(franchiseRepository, times(1)).existsByNameIgnoreCase("Burger King");
            verify(franchiseRepository, times(1)).save(any(Franchise.class));
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException cuando el nombre ya existe")
        void createFranchise_duplicateName_throwsDuplicateException() {
            FranchiseRequest request = new FranchiseRequest();
            request.setName("McDonald's");

            when(franchiseRepository.existsByNameIgnoreCase("McDonald's"))
                    .thenReturn(Mono.just(true));

            StepVerifier.create(franchiseService.createFranchise(request))
                    .expectErrorMatches(error ->
                            error instanceof DuplicateResourceException &&
                                    error.getMessage().contains("McDonald's"))
                    .verify();

            verify(franchiseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("listFranchises()")
    class ListFranchises {

        @Test
        @DisplayName("Debe retornar todas las franquicias existentes")
        void listFranchises_returnsList() {
            Franchise franchise2 = Franchise.builder()
                    .id("franchise-id-2")
                    .name("KFC")
                    .branches(new ArrayList<>())
                    .build();

            FranchiseResponse franchiseResponse2 = FranchiseResponse.builder()
                    .id("franchise-id-2")
                    .name("KFC")
                    .build();

            when(franchiseRepository.findAll())
                    .thenReturn(Flux.just(franchise, franchise2));
            when(franchiseMapper.toResponse(franchise)).thenReturn(franchiseResponse);
            when(franchiseMapper.toResponse(franchise2)).thenReturn(franchiseResponse2);

            StepVerifier.create(franchiseService.listFranchises())
                    .expectNextMatches(r -> r.getId().equals("franchise-id-1"))
                    .expectNextMatches(r -> r.getId().equals("franchise-id-2"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe retornar Flux vacío cuando no hay franquicias")
        void listFranchises_empty() {
            when(franchiseRepository.findAll()).thenReturn(Flux.empty());

            StepVerifier.create(franchiseService.listFranchises())
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("getFranchise()")
    class GetFranchise {

        @Test
        @DisplayName("Debe retornar franquicia cuando el ID existe")
        void getFranchise_found() {
            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchise));
            when(franchiseMapper.toResponse(franchise))
                    .thenReturn(franchiseResponse);

            StepVerifier.create(franchiseService.getFranchise("franchise-id-1"))
                    .expectNextMatches(r -> r.getId().equals("franchise-id-1"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
        void getFranchise_notFound_throwsException() {
            when(franchiseRepository.findById("id-inexistente"))
                    .thenReturn(Mono.empty()); // Simula documento no encontrado en MongoDB

            StepVerifier.create(franchiseService.getFranchise("id-inexistente"))
                    .expectErrorMatches(error ->
                            error instanceof ResourceNotFoundException &&
                                    error.getMessage().contains("id-inexistente"))
                    .verify();
        }
    }

    @Nested
    @DisplayName("setFranchiseName()")
    class SetFranchiseName {

        @Test
        @DisplayName("Debe actualizar el nombre de la franquicia exitosamente")
        void setFranchiseName_success() {
            FranchiseRequest request = new FranchiseRequest();
            request.setName("McDonald's Renovado");

            FranchiseResponse updatedResponse = FranchiseResponse.builder()
                    .id("franchise-id-1")
                    .name("McDonald's Renovado")
                    .build();

            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.existsByNameIgnoreCase("McDonald's Renovado"))
                    .thenReturn(Mono.just(false));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(franchise));
            when(franchiseMapper.toResponse(any(Franchise.class)))
                    .thenReturn(updatedResponse);

            StepVerifier.create(franchiseService.setFranchiseName("franchise-id-1", request))
                    .expectNextMatches(r -> r.getName().equals("McDonald's Renovado"))
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe lanzar ResourceNotFoundException si la franquicia no existe")
        void setFranchiseName_franchiseNotFound() {
            FranchiseRequest request = new FranchiseRequest();
            request.setName("Nuevo Nombre");

            when(franchiseRepository.findById("id-inexistente"))
                    .thenReturn(Mono.empty());

            StepVerifier.create(franchiseService.setFranchiseName("id-inexistente", request))
                    .expectErrorMatches(error -> error instanceof ResourceNotFoundException)
                    .verify();

            verify(franchiseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar DuplicateResourceException si el nuevo nombre ya está en uso")
        void setFranchiseName_duplicateName_throwsException() {
            FranchiseRequest request = new FranchiseRequest();
            request.setName("KFC");

            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchise));
            when(franchiseRepository.existsByNameIgnoreCase("KFC"))
                    .thenReturn(Mono.just(true));

            StepVerifier.create(franchiseService.setFranchiseName("franchise-id-1", request))
                    .expectErrorMatches(error ->
                            error instanceof DuplicateResourceException &&
                                    error.getMessage().contains("KFC"))
                    .verify();

            verify(franchiseRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("addBranch()")
    class AddBranch {

        @Test
        @DisplayName("Debe agregar sucursal exitosamente a la franquicia")
        void addBranch_success() {
            Franchise franchiseSinSucursales = Franchise.builder()
                    .id("franchise-id-1")
                    .name("McDonald's")
                    .branches(new ArrayList<>())
                    .build();

            BranchRequest request = new BranchRequest();
            request.setName("Nueva Sucursal");

            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchiseSinSucursales));
            when(franchiseRepository.save(any(Franchise.class)))
                    .thenReturn(Mono.just(franchiseSinSucursales));
            when(franchiseMapper.toResponse(any(Branch.class)))
                    .thenReturn(branchResponse);

            StepVerifier.create(franchiseService.addBranch("franchise-id-1", request))
                    .expectNextMatches(r -> r.getId().equals("branch-id-1"))
                    .verifyComplete();

            verify(franchiseRepository, times(1)).save(any(Franchise.class));
        }

        @Test
        @DisplayName("Debe lanzar BusinessException si ya existe sucursal con ese nombre")
        void addBranch_duplicateName_throwsBusinessException() {
            BranchRequest request = new BranchRequest();
            request.setName("Chapinero Branch");

            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchise));

            StepVerifier.create(franchiseService.addBranch("franchise-id-1", request))
                    .expectErrorMatches(error ->
                            error instanceof BusinessException &&
                                    error.getMessage().contains("Chapinero Branch"))
                    .verify();

            verify(franchiseRepository, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar BusinessException con nombre en diferente case (case-insensitive)")
        void addBranch_duplicateNameCaseInsensitive_throwsBusinessException() {
            BranchRequest request = new BranchRequest();
            request.setName("CHAPINERO BRANCH");

            when(franchiseRepository.findById("franchise-id-1"))
                    .thenReturn(Mono.just(franchise));

            StepVerifier.create(franchiseService.addBranch("franchise-id-1", request))
                    .expectErrorMatches(error -> error instanceof BusinessException)
                    .verify();
        }
    }
}
