package com.wedora.service;

import com.wedora.dto.StaffDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Staff;
import com.wedora.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Staff business logic.
 */
@Service
public class StaffService {

    private final StaffRepository staffRepository;

    @Autowired
    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public List<StaffDTO> getAllStaff() throws IOException {
        List<Staff> staffList = staffRepository.findAll();
        return staffList.stream()
                .filter(s -> s != null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<StaffDTO> getActiveStaff() throws IOException {
        List<Staff> staffList = staffRepository.findActive();
        return staffList.stream()
                .filter(s -> s != null)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StaffDTO getStaffById(String id) throws IOException {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        return toDTO(staff);
    }

    public StaffDTO createStaff(StaffDTO staffDTO) throws IOException {
        Staff staff = toEntity(staffDTO);
        staffRepository.save(staff);
        return toDTO(staff);
    }

    public StaffDTO updateStaff(String id, StaffDTO staffDTO) throws IOException {
        Staff existingStaff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));

        existingStaff.setFirstName(staffDTO.getFirstName());
        existingStaff.setLastName(staffDTO.getLastName());
        existingStaff.setEmail(staffDTO.getEmail());
        existingStaff.setPhone(staffDTO.getPhone());
        existingStaff.setPosition(staffDTO.getPosition());
        existingStaff.setHireDate(staffDTO.getHireDate());
        existingStaff.setDepartment(staffDTO.getDepartment());
        existingStaff.setSalary(staffDTO.getSalary());
        existingStaff.setActive(staffDTO.isActive());

        staffRepository.save(existingStaff);
        return toDTO(existingStaff);
    }

    public void archiveStaff(String id) throws IOException {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        staff.archive();
        staffRepository.save(staff);
    }

    public void deleteStaff(String id) throws IOException {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + id));
        staffRepository.delete(id);
    }

    private Staff toEntity(StaffDTO dto) {
        Staff staff = new Staff(dto.getId());
        staff.setFirstName(dto.getFirstName());
        staff.setLastName(dto.getLastName());
        staff.setEmail(dto.getEmail());
        staff.setPhone(dto.getPhone());
        staff.setPosition(dto.getPosition());
        staff.setHireDate(dto.getHireDate());
        staff.setDepartment(dto.getDepartment());
        staff.setSalary(dto.getSalary());
        staff.setActive(dto.isActive());
        return staff;
    }

    private StaffDTO toDTO(Staff staff) {
        return new StaffDTO(
                staff.getId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getEmail(),
                staff.getPhone(),
                staff.getPosition(),
                staff.getHireDate(),
                staff.getDepartment(),
                staff.getSalary(),
                staff.isActive()
        );
    }
}
