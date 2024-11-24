package com.grolabs.caselist.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseUpdateDto {

    private String problemTitle;

    private String product;

    private String version;

    private String serialNumber;

    private String severity;

    private Long userId;

}