package com.grolabs.caselist.controller;

import com.grolabs.caselist.dto.CaseCreateDto;
import com.grolabs.caselist.dto.CaseGetDto;
import com.grolabs.caselist.dto.CaseStatusUpdateDto;
import com.grolabs.caselist.entity.Case;
import com.grolabs.caselist.entity.enums.CaseStatus;
import com.grolabs.caselist.service.CaseService;
import com.grolabs.caselist.dto.CaseUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
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
    @Operation(
            summary = "케이스 생성",
            description = "AccessToken으로 유저를 인식하고, 이에 따른 케이스를 생성하고 해당 User를 저장함",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Case Create Successfully")
            }
    )
    @PostMapping
    public String createCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestBody CaseCreateDto requestDto) {
        return caseService.createCase(accessToken, requestDto);
    }

    /**
     * Get All Cases
     * @return List<CaseGetDto> caseId, problemTitle, product, version, serialNumber, severity, user_id, createAt, caseStatus
     */
    @Operation(
            summary = "모든 케이스 불러오기",
            description = "모든 케이스를 불러옴",
            responses = {
                    @ApiResponse(responseCode = "200", description = "모든 케이스")
            }
    )
    @GetMapping
    public List<CaseGetDto> getAllCases(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken) {
        return caseService.getAllCases(accessToken);
    }

    /**
     * Get One Case
     * @param caseId CaseId of the case to be found
     * @return CaseGetDto
     */
    @Operation(
            summary = "특정 케이스 확인",
            description = "caseId를 통해 특정 케이스에 대한 정보를 불러온다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "특정 케이스 확인"),
                    @ApiResponse(responseCode = "404", description = "글을 찾을 수 없습니다.")
            }
    )
    @GetMapping("/{caseId}")
    public CaseGetDto getCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,@PathVariable Long caseId) {
        return caseService.getCase(accessToken,caseId);
    }

    /**
     * Update Case
     * @param caseId CaseId of the case to be edited
     * @param requestDto product, version, subject, description, userId (Optional, nullable)
     * @return
     */
    @Operation(
            summary = "특정 케이스 확인",
            description = "caseId를 통해 특정 케이스에 대한 정보를 불러온다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "글을 찾을 수 없습니다.")
            }
    )
    @PutMapping("/{caseId}")
    public String updateCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                             @PathVariable Long caseId,
                              @RequestBody CaseUpdateDto requestDto) {
        return caseService.updateCase(accessToken,requestDto, caseId);
    }

    /**
     * Update Case Status
     * @param caseId CaseId of the case to be edited
     * @param caseStatus status
     * @return String
     */
    @Operation(
            summary = "케이스 상태 업데이트",
            description = "특정 케이스의 상태를 사용자가 지정한다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "글을 찾을 수 없습니다.")
            }
    )
    @PutMapping("/{caseId}/status")
    public String updateCaseStatus(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,
                                   @PathVariable Long caseId,
                                   @RequestBody CaseStatusUpdateDto caseStatus) {
        return caseService.updateCaseStatus(accessToken,caseId, caseStatus);
    }


    /**
     * Delete Case
     * @param caseId CaseId of the case to be deleted
     * @return String
     */
    @Operation(
            summary = "케이스 삭제",
            description = "특정 케이스를 삭제한다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "글을 찾을 수 없습니다.")
            }
    )
    @DeleteMapping("/{caseId}")
    public String deleteCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken,@PathVariable Long caseId) {

        return caseService.deleteCase(accessToken,caseId);
    }

    /**
     * Search Case
     * @param keyWord Search from case-list with keyword
     * @return Cases List
     */
    @Operation(
            summary = "케이스 검색",
            description = "keyword를 사용하여 케이스를 검색한다",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공")
            }
    )
    @GetMapping("/search")
    public List<Case> searchCase(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam String keyWord) {
        System.out.println(accessToken);
        return caseService.searchCase(accessToken, keyWord);
    }
}
