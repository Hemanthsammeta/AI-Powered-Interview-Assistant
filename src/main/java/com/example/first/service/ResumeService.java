package com.example.first.service;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ResumeService {

    private final Tika tika = new Tika();

    public Map<String, String> parseResume(MultipartFile file) throws IOException {
        String text = null;
		try {
			text = tika.parseToString(file.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TikaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        Map<String, String> extracted = new HashMap<>();

        // ---- Extract Name (naive: first line of resume) ----
        String[] lines = text.split("\n");
        if (lines.length > 0) {
            extracted.put("name", lines[0].trim());
        }

        // ---- Extract Email ----
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}");
        Matcher emailMatcher = emailPattern.matcher(text);
        if (emailMatcher.find()) {
            extracted.put("email", emailMatcher.group());
        }

        // ---- Extract Phone ----
        Pattern phonePattern = Pattern.compile("\\+?\\d{10,13}");
        Matcher phoneMatcher = phonePattern.matcher(text);
        if (phoneMatcher.find()) {
            extracted.put("phone", phoneMatcher.group());
        }

        return extracted;
    }
}
