package com.grolabs.caselist.controller;

import com.grolabs.caselist.dto.CaseCreateDto;
import com.grolabs.caselist.dto.CaseStatusUpdateDto;
import com.grolabs.caselist.entity.Case;
import com.grolabs.caselist.entity.enums.CaseStatus;
import com.grolabs.caselist.service.CaseService;
import com.grolabs.caselist.dto.CaseUpdateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/board")
@RequiredArgsConstructor
@Slf4j
public class CaseController {
    private final CaseService caseService;

    /**
     * Create Case
     * @param requestDto product, version, subject, description, userId (not null)
     * @return String
     */
    @PostMapping
    public String createCase(@RequestBody CaseCreateDto requestDto) {

        return caseService.createCase(requestDto);
    }

    /**
     * Get All Cases
     * @return Cases List
     */
    @GetMapping
    public List<Case> getAllCases() {
        return caseService.getAllCases();
    }

    /**
     * Get One Case
     * @param caseId CaseId of the case to be found
     * @return case
     */
    @GetMapping("/{caseId}")
    public Case getCase(@PathVariable Long caseId) {

        return caseService.getCase(caseId);
    }

    /**
     * Update Case
     * @param caseId CaseId of the case to be edited
     * @param requestDto product, version, subject, description, userId (Optional, nullable)
     * @return
     */
    @PutMapping("/{caseId}")
    public String updateCase(@PathVariable Long caseId,
                              @RequestBody CaseUpdateDto requestDto) {
        return caseService.updateCase(requestDto, caseId);
    }

    /**
     * Update Case Status
     * @param caseId CaseId of the case to be edited
     * @param caseStatus status
     * @return String
     */
    @PutMapping("/{caseId}/status")
    public String updateCaseStatus(@PathVariable Long caseId,
                                   @RequestBody CaseStatusUpdateDto caseStatus) {
        return caseService.updateCaseStatus(caseId, caseStatus);
    }


    /**
     * Delete Case
     * @param caseId CaseId of the case to be deleted
     * @return String
     */
    @DeleteMapping("/{caseId}")
    public String deleteCase(@PathVariable Long caseId) {

        return caseService.deleteCase(caseId);
    }
}
