package com.grolabs.caselist.service;

import com.grolabs.caselist.dto.CaseStatusUpdateDto;
import com.grolabs.caselist.entity.Case;
import com.grolabs.caselist.entity.enums.CaseStatus;
import com.grolabs.caselist.repository.CaseRepository;
import com.grolabs.caselist.dto.CaseCreateDto;
import com.grolabs.caselist.dto.CaseUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CaseService {
    public final CaseRepository caseRepository;

    public static final String BOARD_NOT_FOUND = "글을 찾을 수 없습니다.";

    /**
     * Create Case
     * @param requestDto product, version, subject, description, userId (not null)
     * @return String
     */
    public String createCase(CaseCreateDto requestDto) {
        Case aCase = new Case(requestDto);

        caseRepository.save(aCase);

        return "Case Create Successfully";
    }

    /**
     * Get All Cases
     * @return Cases List
     */
    public List<Case> getAllCases() {

        return caseRepository.findAll();
    }

    /**
     * Get One Case
     * @param caseId CaseId of the case to be found
     * @return case
     */
    public Case getCase(Long caseId) {

        return caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));
    }

    /**
     * Update Case
     * @param requestDto product, version, subject, description, userId (Optional, nullable)
     * @param caseId CaseId of the case to be edited
     * @return String
     */
    public String updateCase(CaseUpdateDto requestDto, Long caseId) {
        Case currentCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));

        if (requestDto.getProduct() != null) {
            currentCase.setProduct(requestDto.getProduct());
        }
        if (requestDto.getVersion() != null) {
            currentCase.setVersion(requestDto.getVersion());
        }
        if (requestDto.getSubject() != null) {
            currentCase.setSubject(requestDto.getSubject());
        }
        if (requestDto.getDescription() != null) {
            currentCase.setDescription(requestDto.getDescription());
        }
        if (requestDto.getUserId() != null) {
            currentCase.setUserId(requestDto.getUserId());
        }

        currentCase.setUpdatedAt(LocalDateTime.now());

        caseRepository.save(currentCase);

        return "Case Updated Successfully";

    }

    /**
     * Update Case Status
     * @param caseId CaseId of the case to be edited
     * @param caseStatus status
     * @return String
     */
    public String updateCaseStatus(Long caseId, CaseStatusUpdateDto requestDto) {
        Case currentCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));

        currentCase.setCaseStatus(requestDto.getCaseStatus());

        currentCase.setUpdatedAt(LocalDateTime.now());


        caseRepository.save(currentCase);

        return "Case Status Updated Successfully";
    }

    /**
     * Delete Case
     * @param caseId CaseId of the case to be deleted
     * @return String
     */
    public String deleteCase(Long caseId) {
        Case currentCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));

        caseRepository.delete(currentCase);

        return "Case Deleted Successfully";
    }
}
