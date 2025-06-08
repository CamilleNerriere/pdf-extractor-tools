package com.noesis.pdf_extractor_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.noesis.pdf_extractor_tools.service.JwtService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ExtractionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    private String token;

    @BeforeEach
    void setup() {
        UserDetails userDetails = User.builder().username("demo.user@example.com").password("superSecretPassword")
                .roles("USER").build();
        token = jwtService.generateToken(userDetails);
    }

    @Test
    void shouldReturnZipFilesForValidAnnotationRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", getClass().getResourceAsStream("/test.pdf"));

        mockMvc.perform(multipart("/extract/annotations")
                .file(file)
                .param("formats", "txt")
                .param("title", "Test PDF")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/zip"));
    }

    @Test
    void shouldReturnZipFilesForValidCitationRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", getClass().getResourceAsStream("/test.pdf"));

        mockMvc.perform(multipart("/extract/citations")
                .file(file)
                .param("formats", "txt")
                .param("title", "Test PDF")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/zip"));
    }
}
