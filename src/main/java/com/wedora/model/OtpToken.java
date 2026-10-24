package com.wedora.model;

import java.time.LocalDateTime;

/**
 * OTP Token entity for secure authentication.
 */
public class OtpToken extends BaseEntity {
    private String email;
    private String otpCode;
    private LocalDateTime expiryTime;
    private boolean used;
    private String sessionId;

    public OtpToken() {
        super();
        this.used = false;
    }

    public OtpToken(String id) {
        super(id);
        this.used = false;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(LocalDateTime expiryTime) {
        this.expiryTime = expiryTime;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Checks if OTP is expired.
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }

    /**
     * Checks if OTP is valid (not expired and not used).
     */
    public boolean isValid() {
        return !isExpired() && !isUsed();
    }

    /**
     * Marks OTP as used.
     */
    public void markAsUsed() {
        this.used = true;
        this.markAsUpdated();
    }

    @Override
    public String toString() {
        return "OtpToken{" +
                "email='" + email + '\'' +
                ", expiryTime=" + expiryTime +
                ", used=" + used +
                '}';
    }
}
