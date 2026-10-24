package com.wedora.service;

import com.wedora.model.OtpToken;
import com.wedora.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Service for OTP generation and validation.
 */
@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;
    private final SecureRandom secureRandom;

    @Autowired
    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
        this.secureRandom = new SecureRandom();
    }

    /**
     * Generates and saves a new OTP for the given email.
     * Returns the generated OTP code.
     */
    public String generateOtp(String email) throws IOException {
        // Clean up expired tokens first
        otpRepository.cleanupExpired();

        String otpCode = generateOtpCode();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        OtpToken token = new OtpToken();
        token.setEmail(email);
        token.setOtpCode(otpCode);
        token.setExpiryTime(expiryTime);
        token.setUsed(false);

        otpRepository.save(token);

        return otpCode;
    }

    /**
     * Validates the OTP code for the given email.
     */
    public boolean validateOtp(String email, String otpCode) throws IOException {
        OtpToken token = otpRepository.findByEmail(email).orElse(null);

        if (token == null) {
            return false;
        }

        if (!token.getOtpCode().equals(otpCode)) {
            return false;
        }

        if (!token.isValid()) {
            return false;
        }

        // Mark as used after successful validation
        otpRepository.markAsUsed(otpCode);
        return true;
    }

    /**
     * Generates a random 6-digit OTP code.
     */
    private String generateOtpCode() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    /**
     * Gets the expiry time in minutes for OTP.
     */
    public int getOtpExpiryMinutes() {
        return OTP_EXPIRY_MINUTES;
    }
}
