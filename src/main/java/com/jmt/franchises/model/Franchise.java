package com.jmt.franchises.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "franchises")
public class Franchise {

    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    @Builder.Default
    private List<Branch> branches = new ArrayList<>();
}
