package com.example.first.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "https://rococo-moxie-d2ce47.netlify.app/")
public class ResumeController {

    // Minimal parser: extract first email & phone found (naive)
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        try {
            String name = StringUtils.cleanPath(file.getOriginalFilename());
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);

            // simple regex for email and phone (basic)
            Pattern emailPat = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-z]{2,}");
            Pattern phonePat = Pattern.compile("\\+?\\d[\\d\\s\\-]{6,}\\d");

            Matcher mEmail = emailPat.matcher(text);
            Matcher mPhone = phonePat.matcher(text);

            String email = mEmail.find() ? mEmail.group() : "";
            String phone = mPhone.find() ? mPhone.group() : "";

            // name detection is hard; return filename as fallback
            String candidateName = name;

            return ResponseEntity.ok(Map.of(
                    "name", candidateName,
                    "email", email,
                    "phone", phone,
                    "resumeFileId", name
            ));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Failed to parse resume"));
        }
    }
}

