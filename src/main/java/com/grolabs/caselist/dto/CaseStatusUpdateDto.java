package com.grolabs.caselist.dto;

import com.grolabs.caselist.entity.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseStatusUpdateDto {
    private CaseStatus caseStatus;
}
