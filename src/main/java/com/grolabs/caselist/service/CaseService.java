package com.grolabs.caselist.service;

import com.grolabs.caselist.dto.CaseGetDto;
import com.grolabs.caselist.dto.CaseStatusUpdateDto;
import com.grolabs.caselist.entity.Case;
import com.grolabs.caselist.entity.User;
import com.grolabs.caselist.entity.enums.CaseStatus;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.CaseRepository;
import com.grolabs.caselist.dto.CaseCreateDto;
import com.grolabs.caselist.dto.CaseUpdateDto;
import com.grolabs.caselist.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CaseService {
    public final CaseRepository caseRepository;

    public final UserRepository userRepository;

    public final JWTUtil jwtUtil;

    public static final String BOARD_NOT_FOUND = "글을 찾을 수 없습니다.";
    public static final String USER_NOT_FOUND = "유저를 찾을 수 없습니다.";

    /**
     * Create Case
     * @param requestDto product, version, subject, description, userId (not null)
     * @return String
     */
    public String createCase(String accessToken, CaseCreateDto requestDto) {
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

        Case aCase = new Case(requestDto, user);

        caseRepository.save(aCase);

        return "Case Create Successfully";
    }

    /**
     * Get All Cases
     *
     * @return List<CaseGetDto> caseId, problemTitle, product, version, serialNumber, severity, user_id, createAt, caseStatus
     */
    public List<CaseGetDto> getAllCases(String accessToken) {

        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);
        if(user==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

        List<Case> AllCases = caseRepository.findAllByUserId(user.getId());

        List<CaseGetDto> cases = new ArrayList<>();
        for (Case aCase : AllCases) {
            cases.add(new CaseGetDto(aCase, aCase.getUser()));
        }
        return cases;
    }

    /**
     * Get One Case
     * @param caseId CaseId of the case to be found
     * @return CaseGetDto
     */
    public CaseGetDto getCase(String accessToken, Long caseId) {
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        if(username==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

        Case currentcase= caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));
        return new CaseGetDto(currentcase, currentcase.getUser());
    }

    /**
     * Update Case
     * @param requestDto product, version, subject, description, userId (Optional, nullable)
     * @param caseId CaseId of the case to be edited
     * @return String
     */
    public String updateCase(String accessToken, CaseUpdateDto requestDto, Long caseId) {
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        if(username==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

        Case currentCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));

        if (requestDto.getProduct() != null) {
            currentCase.setProduct(requestDto.getProduct());
        }
        if (requestDto.getVersion() != null) {
            currentCase.setVersion(requestDto.getVersion());
        }
        if (requestDto.getProblemTitle() != null) {
            currentCase.setProblemTitle(requestDto.getProblemTitle());
        }
        if (requestDto.getSerialNumber() != null) {
            currentCase.setSerialNumber(requestDto.getSerialNumber());
        }
        if (requestDto.getSeverity() != null) {
            currentCase.setSeverity(requestDto.getSeverity());
        }

        currentCase.setUpdatedAt(LocalDateTime.now());

        caseRepository.save(currentCase);

        return "Case Updated Successfully";

    }

    /**
     * Update Case Status
     * @param caseId CaseId of the case to be edited
     * @param requestDto status
     * @return String
     */
    public String updateCaseStatus(String accessToken, Long caseId, CaseStatusUpdateDto requestDto) {
        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        if(username==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

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
    public String deleteCase(String accessToken, Long caseId) {

        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);
        if(username==null){
            throw new NoSuchElementException(USER_NOT_FOUND);
        }

        Case currentCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException(BOARD_NOT_FOUND));

        caseRepository.delete(currentCase);

        return "Case Deleted Successfully";
    }


    /**
     * Search Case
     * @param keyWord Search from case-list with keyword
     * @return Cases List
     */
    public List<Case> searchCase(String accessToken, String keyWord) {

        String token = accessToken.split(" ")[1];
        String username = jwtUtil.getUsername(token);

        User user = userRepository.findByUsername(username);

        List<Case> cases = caseRepository.findAllByUserId(user.getId());

        return cases.stream()
                .filter(c->containsKeywordInFields(c, keyWord))
                .collect(Collectors.toList());
    }

    private boolean containsKeywordInFields(Case c, String keyword) {
        // 필드에 해당 키워드가 포함되어 있는지 검사(대소문자 관계 없이)
        return (c.getProduct() != null && c.getProduct().toLowerCase().contains(keyword.toLowerCase())) ||
                (c.getVersion() != null && c.getVersion().toLowerCase().contains(keyword.toLowerCase())) ||
                (c.getProblemTitle() != null && c.getProblemTitle().toLowerCase().contains(keyword.toLowerCase())) ||
                (c.getSeverity() != null && c.getSeverity().toLowerCase().contains(keyword.toLowerCase()));
    }
}
