package com.wedora.repository;

import com.wedora.model.Vendor;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Vendor persistence using TXT files.
 */
@Repository
public class VendorRepository {

    private static final String VENDORS_FILE = "vendors.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Finds all vendors.
     */
    public List<Vendor> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(VENDORS_FILE);
        List<Vendor> vendors = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Vendor vendor = fromString(line);
                    vendors.add(vendor);
                } catch (Exception e) {
                    System.err.println("Error parsing vendor line: " + line);
                }
            }
        }

        return vendors;
    }

    /**
     * Finds vendor by ID.
     */
    public Optional<Vendor> findById(String id) throws IOException {
        List<Vendor> vendors = findAll();
        return vendors.stream()
                .filter(v -> v.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds active vendors only.
     */
    public List<Vendor> findActive() throws IOException {
        return findAll().stream()
                .filter(Vendor::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Finds vendors by category.
     */
    public List<Vendor> findByCategory(String category) throws IOException {
        return findAll().stream()
                .filter(v -> v.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Searches vendors by name.
     */
    public List<Vendor> searchByName(String searchTerm) throws IOException {
        String lowerTerm = searchTerm.toLowerCase();
        return findAll().stream()
                .filter(v -> v.getName().toLowerCase().contains(lowerTerm))
                .collect(Collectors.toList());
    }

    /**
     * Saves a vendor (create or update).
     */
    public void save(Vendor vendor) throws IOException {
        List<Vendor> vendors = findAll();

        boolean exists = vendors.stream()
                .anyMatch(v -> v.getId().equals(vendor.getId()));

        if (exists) {
            vendors = vendors.stream()
                    .map(v -> v.getId().equals(vendor.getId()) ? vendor : v)
                    .collect(Collectors.toList());
        } else {
            vendors.add(vendor);
        }

        vendor.markAsUpdated();
        writeAll(vendors);
    }

    /**
     * Deletes a vendor by ID (soft delete - archives instead).
     */
    public void delete(String id) throws IOException {
        Optional<Vendor> vendorOpt = findById(id);
        if (vendorOpt.isPresent()) {
            Vendor vendor = vendorOpt.get();
            vendor.archive();
            save(vendor);
        }
    }

    /**
     * Counts total vendors.
     */
    public long count() throws IOException {
        return findAll().size();
    }

    /**
     * Counts active vendors.
     */
    public long countActive() throws IOException {
        return findActive().size();
    }

    /**
     * Writes all vendors to file.
     */
    private void writeAll(List<Vendor> vendors) throws IOException {
        List<String> lines = vendors.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(VENDORS_FILE, lines);
    }

    /**
     * Converts Vendor to string for file storage.
     * Format: id|name|category|contactPerson|email|phone|address|serviceDescription|pricing|active|createdAt|updatedAt
     */
    private String toString(Vendor vendor) {
        return String.join("|",
                vendor.getId(),
                escape(vendor.getName()),
                escape(vendor.getCategory()),
                escape(vendor.getContactPerson()),
                escape(vendor.getEmail()),
                escape(vendor.getPhone()),
                escape(vendor.getAddress()),
                escape(vendor.getServiceDescription()),
                escape(vendor.getPricing()),
                String.valueOf(vendor.isActive()),
                vendor.getCreatedAt().format(FORMATTER),
                vendor.getUpdatedAt().format(FORMATTER)
        );
    }

    /**
     * Converts string from file to Vendor.
     */
    private Vendor fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 12) {
            throw new IllegalArgumentException("Invalid vendor data format");
        }

        Vendor vendor = new Vendor(parts[0]);
        vendor.setName(unescape(parts[1]));
        vendor.setCategory(unescape(parts[2]));
        vendor.setContactPerson(unescape(parts[3]));
        vendor.setEmail(unescape(parts[4]));
        vendor.setPhone(unescape(parts[5]));
        vendor.setAddress(unescape(parts[6]));
        vendor.setServiceDescription(unescape(parts[7]));
        vendor.setPricing(unescape(parts[8]));
        vendor.setActive(Boolean.parseBoolean(parts[9]));
        vendor.setCreatedAt(java.time.LocalDateTime.parse(parts[10], FORMATTER));
        vendor.setUpdatedAt(java.time.LocalDateTime.parse(parts[11], FORMATTER));

        return vendor;
    }

    private String escape(String value) {
        if (value == null) return "";
        return value.replace("|", "||");
    }

    private String unescape(String value) {
        if (value == null || value.isEmpty()) return null;
        return value.replace("||", "|");
    }
}
