package com.grolabs.caselist.dto;

import com.grolabs.caselist.entity.Case;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCreateDto {

    private String problemTitle;

    private String product;

    private String version;

    private String serialNumber;

    private String Severity;

    private Long userId;


    public CaseCreateDto(Case aCase) {
        this(
                aCase.getProblemTitle(),
                aCase.getProduct(),
                aCase.getVersion(),
                aCase.getSerialNumber(),
                aCase.getSeverity(),
                aCase.getUserId()
        );
    }
}
