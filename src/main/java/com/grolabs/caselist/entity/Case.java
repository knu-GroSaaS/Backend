package com.grolabs.caselist.entity;

import com.grolabs.caselist.dto.CaseCreateDto;
import com.grolabs.caselist.entity.enums.CaseStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import static com.grolabs.caselist.entity.enums.CaseStatus.NOT_STARTED;

@Entity
@Table(name = "case_list")
@Getter
@Setter
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "problemTitle", nullable = false)
    private String problemTitle;

    @Column(name = "product", nullable = false)
    private String product;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "serialNumber", nullable = false)
    private String serialNumber;

    @Column(name = "severity", nullable = false)
    private String severity;

    @Column(name = "userId", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CaseStatus caseStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public Case() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Case(CaseCreateDto requestDto) {
        this.problemTitle = requestDto.getProblemTitle();
        this.product = requestDto.getProduct();
        this.version = requestDto.getVersion();
        this.serialNumber = requestDto.getSerialNumber();
        this.severity = requestDto.getSeverity();
        this.userId = requestDto.getUserId();
        this.caseStatus = NOT_STARTED;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
