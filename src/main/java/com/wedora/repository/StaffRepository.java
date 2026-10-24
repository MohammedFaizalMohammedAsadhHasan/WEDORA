package com.wedora.repository;

import com.wedora.model.Staff;
import com.wedora.util.FileStorageUtil;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository for Staff persistence using TXT files.
 */
@Repository
public class StaffRepository {

    private static final String STAFF_FILE = "staff.txt";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public List<Staff> findAll() throws IOException {
        List<String> lines = FileStorageUtil.readAllLines(STAFF_FILE);
        List<Staff> staffList = new ArrayList();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                try {
                    Staff staff = fromString(line);
                    staffList.add(staff);
                } catch (Exception e) {
                    System.err.println("Error parsing staff line: " + line);
                }
            }
        }

        return staffList;
    }

    public Optional<Staff> findById(String id) throws IOException {
        List<Staff> staffList = findAll();
        return staffList.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public List<Staff> findActive() throws IOException {
        return findAll().stream()
                .filter(Staff::isActive)
                .collect(Collectors.toList());
    }

    public List<Staff> findByDepartment(String department) throws IOException {
        return findAll().stream()
                .filter(s -> s.getDepartment() != null && s.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toList());
    }

    public void save(Staff staff) throws IOException {
        List<Staff> staffList = findAll();

        boolean exists = staffList.stream()
                .anyMatch(s -> s.getId().equals(staff.getId()));

        if (exists) {
            staffList = staffList.stream()
                    .map(s -> s.getId().equals(staff.getId()) ? staff : s)
                    .collect(Collectors.toList());
        } else {
            staffList.add(staff);
        }

        staff.markAsUpdated();
        writeAll(staffList);
    }

    public void delete(String id) throws IOException {
        List<Staff> staffList = findAll();
        staffList = staffList.stream()
                .filter(s -> !s.getId().equals(id))
                .collect(Collectors.toList());
        writeAll(staffList);
    }

    private void writeAll(List<Staff> staffList) throws IOException {
        List<String> lines = staffList.stream()
                .map(this::toString)
                .collect(Collectors.toList());
        FileStorageUtil.writeLines(STAFF_FILE, lines);
    }

    private String toString(Staff staff) {
        return String.join("|",
                staff.getId(),
                escape(staff.getFirstName()),
                escape(staff.getLastName()),
                escape(staff.getEmail()),
                escape(staff.getPhone()),
                escape(staff.getPosition()),
                staff.getHireDate() != null ? staff.getHireDate().format(DATE_FORMATTER) : "",
                escape(staff.getDepartment()),
                staff.getSalary() != null ? staff.getSalary().toString() : "",
                String.valueOf(staff.isActive()),
                staff.getCreatedAt().format(DATETIME_FORMATTER),
                staff.getUpdatedAt().format(DATETIME_FORMATTER)
        );
    }

    private Staff fromString(String line) {
        String[] parts = line.split("\\|");
        if (parts.length < 12) {
            throw new IllegalArgumentException("Invalid staff data format");
        }

        Staff staff = new Staff(parts[0]);
        staff.setFirstName(unescape(parts[1]));
        staff.setLastName(unescape(parts[2]));
        staff.setEmail(unescape(parts[3]));
        staff.setPhone(unescape(parts[4]));
        staff.setPosition(unescape(parts[5]));
        staff.setHireDate(parts[6].isEmpty() ? null : LocalDate.parse(parts[6], DATE_FORMATTER));
        staff.setDepartment(unescape(parts[7]));
        staff.setSalary(parts[8].isEmpty() ? null : new BigDecimal(parts[8]));
        staff.setActive(Boolean.parseBoolean(parts[9]));
        staff.setCreatedAt(java.time.LocalDateTime.parse(parts[10], DATETIME_FORMATTER));
        staff.setUpdatedAt(java.time.LocalDateTime.parse(parts[11], DATETIME_FORMATTER));

        return staff;
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
