package com.jmt.franchises.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Branch {

    @Builder.Default
    private String id = UUID.randomUUID().toString();

    private String name;

    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
