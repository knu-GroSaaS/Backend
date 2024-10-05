package com.grolabs.caselist.dto;

import com.grolabs.caselist.entity.Case;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCreateDto {


    private String product;

    private String version;

    private String subject;

    private String description;

    private Long userId;


    public CaseCreateDto(Case aCase) {
        this(
                aCase.getProduct(),
                aCase.getVersion(),
                aCase.getSubject(),
                aCase.getDescription(),
                aCase.getUserId()
        );
    }
}
