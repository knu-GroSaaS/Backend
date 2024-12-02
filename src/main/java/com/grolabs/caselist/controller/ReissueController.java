package com.grolabs.caselist.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grolabs.caselist.entity.RefreshEntity;
import com.grolabs.caselist.jwt.JWTUtil;
import com.grolabs.caselist.repository.RefreshEntityRepository;
import com.grolabs.caselist.service.ReissueService;
import io.jsonwebtoken.ExpiredJwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@ResponseBody
@RequiredArgsConstructor
public class ReissueController {
    public final ReissueService reissueService;

    /**
     * Refresh Token Reissue api
     * @param request The HTTP request containing the refresh token in the header
     * @param response The HTTP response to send the reissued tokens or error messages
     * @return ResponseEntity<?> A response entity indicating the success or failure of the operation
     */
    @Operation(
            summary = "토큰 갱신",
            description = "Refresh토큰을 이용하여 토큰을 갱신한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공"),
                    @ApiResponse(responseCode = "404", description = "토큰이 만료되거나 유효하지 않을 때")
            }
    )
    @PostMapping("api/auth/refresh")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return reissueService.reissue(request, response);
    }


}
