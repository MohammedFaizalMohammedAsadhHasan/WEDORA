package com.wedora.service;

import com.wedora.dto.VendorDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Vendor;
import com.wedora.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Vendor business logic.
 */
@Service
public class VendorService {

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorService(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    /**
     * Gets all vendors.
     */
    public List<VendorDTO> getAllVendors() throws IOException {
        List<Vendor> vendors = vendorRepository.findAll();
        return vendors.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets active vendors only.
     */
    public List<VendorDTO> getActiveVendors() throws IOException {
        List<Vendor> vendors = vendorRepository.findActive();
        return vendors.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets vendor by ID.
     */
    public VendorDTO getVendorById(String id) throws IOException {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        return toDTO(vendor);
    }

    /**
     * Gets vendors by category.
     */
    public List<VendorDTO> getVendorsByCategory(String category) throws IOException {
        List<Vendor> vendors = vendorRepository.findByCategory(category);
        return vendors.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Searches vendors by name.
     */
    public List<VendorDTO> searchByName(String searchTerm) throws IOException {
        List<Vendor> vendors = vendorRepository.searchByName(searchTerm);
        return vendors.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new vendor.
     */
    public VendorDTO createVendor(VendorDTO vendorDTO) throws IOException {
        Vendor vendor = toEntity(vendorDTO);
        vendorRepository.save(vendor);
        return toDTO(vendor);
    }

    /**
     * Updates an existing vendor.
     */
    public VendorDTO updateVendor(String id, VendorDTO vendorDTO) throws IOException {
        Vendor existingVendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));

        existingVendor.setName(vendorDTO.getName());
        existingVendor.setCategory(vendorDTO.getCategory());
        existingVendor.setContactPerson(vendorDTO.getContactPerson());
        existingVendor.setEmail(vendorDTO.getEmail());
        existingVendor.setPhone(vendorDTO.getPhone());
        existingVendor.setAddress(vendorDTO.getAddress());
        existingVendor.setServiceDescription(vendorDTO.getServiceDescription());
        existingVendor.setPricing(vendorDTO.getPricing());
        existingVendor.setActive(vendorDTO.isActive());

        vendorRepository.save(existingVendor);
        return toDTO(existingVendor);
    }

    /**
     * Archives (soft deletes) a vendor.
     */
    public void deleteVendor(String id) throws IOException {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + id));
        vendorRepository.delete(id);
    }

    /**
     * Gets vendor count.
     */
    public long getVendorCount() throws IOException {
        return vendorRepository.count();
    }

    /**
     * Gets active vendor count.
     */
    public long getActiveVendorCount() throws IOException {
        return vendorRepository.countActive();
    }

    /**
     * Converts DTO to Entity.
     */
    private Vendor toEntity(VendorDTO dto) {
        Vendor vendor = new Vendor(dto.getId());
        vendor.setName(dto.getName());
        vendor.setCategory(dto.getCategory());
        vendor.setContactPerson(dto.getContactPerson());
        vendor.setEmail(dto.getEmail());
        vendor.setPhone(dto.getPhone());
        vendor.setAddress(dto.getAddress());
        vendor.setServiceDescription(dto.getServiceDescription());
        vendor.setPricing(dto.getPricing());
        vendor.setActive(dto.isActive());
        return vendor;
    }

    /**
     * Converts Entity to DTO.
     */
    private VendorDTO toDTO(Vendor vendor) {
        return new VendorDTO(
                vendor.getId(),
                vendor.getName(),
                vendor.getCategory(),
                vendor.getContactPerson(),
                vendor.getEmail(),
                vendor.getPhone(),
                vendor.getAddress(),
                vendor.getServiceDescription(),
                vendor.getPricing(),
                vendor.isActive()
        );
    }
}
