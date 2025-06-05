package com.noesis.pdf_extractor_tools.dto.auth;

import org.springframework.http.ResponseEntity;

import com.noesis.pdf_extractor_tools.exception.ErrorResponse;

public record AuthCheckResult(boolean valid, String token, ResponseEntity<ErrorResponse> error){}
