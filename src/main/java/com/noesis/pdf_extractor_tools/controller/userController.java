package com.noesis.pdf_extractor_tools.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.noesis.pdf_extractor_tools.dto.user.UserInfosDto;
import com.noesis.pdf_extractor_tools.exception.ErrorResponse;
import com.noesis.pdf_extractor_tools.service.JwtService;
import com.noesis.pdf_extractor_tools.service.UserService;
import com.noesis.pdf_extractor_tools.web.util.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class userController {

    @Autowired
    JwtService jwtService;
    @Autowired
    UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(HttpServletRequest request) {
        String token = JwtUtils.extractToken(request);

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Unauthorized."));
        }

        String email = jwtService.extractEmail(token);

        UserInfosDto userInfos = userService.getUser(email);

        if (userInfos == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Failed to load user infos"));
        }

        return ResponseEntity.ok().body(userInfos);
    }

}
