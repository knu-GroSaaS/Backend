package com.grolabs.caselist.dto;

import com.grolabs.caselist.entity.Case;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.CaseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseGetDto {
    private Long caseId;
    private String problemTitle;
    private String product;
    private String version;
    private String serialNumber;
    private String severity;
    private Long user_id;
    private CaseStatus caseStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CaseGetDto(Case caseInfo, User user) {
        this.caseId=caseInfo.getCaseId();
        this.problemTitle = caseInfo.getProblemTitle();
        this.product = caseInfo.getProduct();
        this.version = caseInfo.getVersion();
        this.serialNumber = caseInfo.getSerialNumber();
        this.severity = caseInfo.getSeverity();
        this.user_id = user.getId();
        this.createdAt = caseInfo.getCreatedAt();
        this.updatedAt = caseInfo.getUpdatedAt();
        this.caseStatus = caseInfo.getCaseStatus();
    }
}
