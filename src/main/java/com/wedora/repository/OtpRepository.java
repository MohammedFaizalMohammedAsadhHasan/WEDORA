package com.wedora.repository;

import com.wedora.model.OtpToken;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for OTP Token persistence using TXT files.
 */
@Repository
public class OtpRepository {

    private static final String OTP_FILE = "otp_tokens.txt";
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<OtpToken> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(OTP_FILE);
        List<OtpToken> tokens = new ArrayList();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    OtpToken token = fromString(line);
                    tokens.add(token);
                } catch (Exception e) {
                    System.err.println("Error parsing OTP line: " + line);
                }
            }
        }

        return tokens;
    }

    public Optional<OtpToken> findByEmail(String email) throws IOException {
        List<OtpToken> tokens = findAll();
        return tokens.stream()
                .filter(t -> t.getEmail().equals(email))
                .filter(OtpToken::isValid)
                .findFirst();
    }

    public Optional<OtpToken> findByOtpCode(String otpCode) throws IOException {
        List<OtpToken> tokens = findAll();
        return tokens.stream()
                .filter(t -> t.getOtpCode().equals(otpCode))
                .filter(OtpToken::isValid)
                .findFirst();
    }

    public void save(OtpToken token) throws IOException {
        List<OtpToken> tokens = findAll();

        // Remove any existing valid tokens for this email
        tokens = tokens.stream()
                .filter(t -> !t.getEmail().equals(token.getEmail()) || !t.isValid())
                .collect(Collectors.toList());

        tokens.add(token);
        writeAll(tokens);
    }

    public void markAsUsed(String otpCode) throws IOException {
        List<OtpToken> tokens = findAll();
        List<OtpToken> updatedTokens = new ArrayList<>();

        for (OtpToken token : tokens) {
            if (token.getOtpCode().equals(otpCode)) {
                OtpToken updatedToken = new OtpToken(token.getId());
                updatedToken.setEmail(token.getEmail());
                updatedToken.setOtpCode(token.getOtpCode());
                updatedToken.setExpiryTime(token.getExpiryTime());
                updatedToken.setUsed(true);
                updatedToken.setSessionId(token.getSessionId());
                updatedToken.setCreatedAt(token.getCreatedAt());
                updatedToken.setUpdatedAt(token.getUpdatedAt());
                updatedTokens.add(updatedToken);
            } else {
                updatedTokens.add(token);
            }
        }

        writeAll(updatedTokens);
    }

    /**
     * Cleanup expired tokens periodically.
     */
    public void cleanupExpired() throws IOException {
        List<OtpToken> tokens = findAll();
        tokens = tokens.stream()
                .filter(t -> !t.isExpired())
                .collect(Collectors.toList());
        writeAll(tokens);
    }

    private void writeAll(List<OtpToken> tokens) throws IOException {
        List<String> lines = tokens.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(OTP_FILE, lines);
    }

    private String toString(OtpToken token) {
        return String.join("|",
                token.getId(),
                token.getEmail(),
                token.getOtpCode(),
                token.getExpiryTime().format(DATETIME_FORMATTER),
                String.valueOf(token.isUsed()),
                token.getSessionId() != null ? token.getSessionId() : "",
                token.getCreatedAt().format(DATETIME_FORMATTER),
                token.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    private OtpToken fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid OTP token data format");
        }

        OtpToken token = new OtpToken(parts[0]);
        token.setEmail(parts[1]);
        token.setOtpCode(parts[2]);
        token.setExpiryTime(LocalDateTime.parse(parts[3], DATETIME_FORMATTER));
        token.setUsed(Boolean.parseBoolean(parts[4]));
        token.setSessionId(parts[5].isEmpty() ? null : parts[5]);
        token.setCreatedAt(LocalDateTime.parse(parts[6], DATETIME_FORMATTER));
        token.setUpdatedAt(LocalDateTime.parse(parts[7], DATETIME_FORMATTER));

        return token;
    }
}
