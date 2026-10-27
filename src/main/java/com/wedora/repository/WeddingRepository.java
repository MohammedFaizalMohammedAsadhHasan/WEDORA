package com.wedora.repository;

import com.wedora.model.Wedding;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Wedding persistence using TXT files.
 */
@Repository
public class WeddingRepository {

    private static final String WEDDINGS_FILE = "weddings.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Finds all weddings.
     */
    public List<Wedding> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(WEDDINGS_FILE);
        List<Wedding> weddings = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Wedding wedding = fromString(line);
                    weddings.add(wedding);
                } catch (Exception e) {
                    System.err.println("Error parsing wedding line: " + line);
                }
            }
        }

        return weddings;
    }

    /**
     * Finds wedding by ID.
     */
    public Optional<Wedding> findById(String id) throws IOException {
        List<Wedding> weddings = findAll();
        return weddings.stream()
                .filter(w -> w.getId().equals(id))
                .findFirst();
    }

    /**
     * Finds weddings by customer ID.
     */
    public List<Wedding> findByCustomerId(String customerId) throws IOException {
        return findAll().stream()
                .filter(w -> w.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    /**
     * Finds upcoming weddings.
     */
    public List<Wedding> findUpcoming() throws IOException {
        return findAll().stream()
                .filter(w -> w.getWeddingDate() != null && w.isUpcoming())
                .collect(Collectors.toList());
    }

    /**
     * Finds weddings by status.
     */
    public List<Wedding> findByStatus(Wedding.WeddingStatus status) throws IOException {
        return findAll().stream()
                .filter(w -> w.getStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Searches weddings by names.
     */
    public List<Wedding> searchByName(String searchTerm) throws IOException {
        String lowerTerm = searchTerm.toLowerCase();
        return findAll().stream()
                .filter(w -> (w.getBrideName() != null && w.getBrideName().toLowerCase().contains(lowerTerm)) ||
                             (w.getGroomName() != null && w.getGroomName().toLowerCase().contains(lowerTerm)))
                .collect(Collectors.toList());
    }

    /**
     * Saves a wedding (create or update).
     */
    public void save(Wedding wedding) throws IOException {
        List<Wedding> weddings = findAll();

        boolean exists = weddings.stream()
                .anyMatch(w -> w.getId().equals(wedding.getId()));

        if (exists) {
            weddings = weddings.stream()
                    .map(w -> w.getId().equals(wedding.getId()) ? wedding : w)
                    .collect(Collectors.toList());
        } else {
            weddings.add(wedding);
        }

        wedding.markAsUpdated();
        writeAll(weddings);
    }

    /**
     * Deletes a wedding by ID.
     */
    public void delete(String id) throws IOException {
        List<Wedding> weddings = findAll();
        weddings = weddings.stream()
                .filter(w -> !w.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(weddings);
    }

    /**
     * Counts total weddings.
     */
    public long count() throws IOException {
        return findAll().size();
    }

    /**
     * Counts upcoming weddings.
     */
    public long countUpcoming() throws IOException {
        return findUpcoming().size();
    }

    /**
     * Writes all weddings to file.
     */
    private void writeAll(List<Wedding> weddings) throws IOException {
        List<String> lines = weddings.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(WEDDINGS_FILE, lines);
    }

    /**
     * Converts Wedding to string for file storage.
     * Format: id|customerId|brideName|groomName|weddingDate|venue|estimatedGuests|status|notes|createdAt|updatedAt
     */
    private String toString(Wedding wedding) {
        return String.join("|",
                wedding.getId(),
                wedding.getCustomerId(),
                escape(wedding.getBrideName()),
                escape(wedding.getGroomName()),
                wedding.getWeddingDate().format(DATE_FORMATTER),
                escape(wedding.getVenue()),
                wedding.getEstimatedGuests() != null ? wedding.getEstimatedGuests().toString() : "",
                wedding.getStatus().name(),
                escape(wedding.getNotes()),
                wedding.getCreatedAt().format(DATETIME_FORMATTER),
                wedding.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    /**
     * Converts string from file to Wedding.
     */
    private Wedding fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 11) {
            throw new IllegalArgumentException("Invalid wedding data format");
        }

        Wedding wedding = new Wedding(parts[0]);
        wedding.setCustomerId(parts[1]);
        wedding.setBrideName(unescape(parts[2]));
        wedding.setGroomName(unescape(parts[3]));
        wedding.setWeddingDate(LocalDate.parse(parts[4], DATE_FORMATTER));
        wedding.setVenue(unescape(parts[5]));
        wedding.setEstimatedGuests(parts[6].isEmpty() ? null : Integer.parseInt(parts[6]));
        wedding.setStatus(Wedding.WeddingStatus.valueOf(parts[7]));
        wedding.setNotes(unescape(parts[8]));
        wedding.setCreatedAt(java.time.LocalDateTime.parse(parts[9], DATETIME_FORMATTER));
        wedding.setUpdatedAt(java.time.LocalDateTime.parse(parts[10], DATETIME_FORMATTER));

        return wedding;
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
