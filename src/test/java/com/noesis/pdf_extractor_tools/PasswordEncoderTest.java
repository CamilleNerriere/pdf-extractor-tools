package com.noesis.pdf_extractor_tools;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class PasswordEncoderTest {
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testArgon2Hashing(){
        String password = "pdfExtractorTools";
        String hash = passwordEncoder.encode(password);

        assertTrue(passwordEncoder.matches(password, hash));
    }
}


