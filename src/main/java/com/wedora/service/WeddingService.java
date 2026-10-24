package com.wedora.service;

import com.wedora.dto.WeddingDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Wedding;
import com.wedora.repository.WeddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Wedding business logic.
 */
@Service
public class WeddingService {

    private final WeddingRepository weddingRepository;

    @Autowired
    public WeddingService(WeddingRepository weddingRepository) {
        this.weddingRepository = weddingRepository;
    }

    /**
     * Gets all weddings.
     */
    public List<WeddingDTO> getAllWeddings() throws IOException {
        List<Wedding> weddings = weddingRepository.findAll();
        return weddings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets upcoming weddings.
     */
    public List<WeddingDTO> getUpcomingWeddings() throws IOException {
        List<Wedding> weddings = weddingRepository.findUpcoming();
        return weddings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets wedding by ID.
     */
    public WeddingDTO getWeddingById(String id) throws IOException {
        Wedding wedding = weddingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wedding not found with id: " + id));
        return toDTO(wedding);
    }

    /**
     * Gets weddings by customer ID.
     */
    public List<WeddingDTO> getWeddingsByCustomerId(String customerId) throws IOException {
        List<Wedding> weddings = weddingRepository.findByCustomerId(customerId);
        return weddings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Searches weddings by names.
     */
    public List<WeddingDTO> searchByName(String searchTerm) throws IOException {
        List<Wedding> weddings = weddingRepository.searchByName(searchTerm);
        return weddings.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new wedding.
     */
    public WeddingDTO createWedding(WeddingDTO weddingDTO) throws IOException {
        Wedding wedding = toEntity(weddingDTO);
        weddingRepository.save(wedding);
        return toDTO(wedding);
    }

    /**
     * Updates an existing wedding.
     */
    public WeddingDTO updateWedding(String id, WeddingDTO weddingDTO) throws IOException {
        Wedding existingWedding = weddingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wedding not found with id: " + id));

        existingWedding.setCustomerId(weddingDTO.getCustomerId());
        existingWedding.setBrideName(weddingDTO.getBrideName());
        existingWedding.setGroomName(weddingDTO.getGroomName());
        existingWedding.setWeddingDate(weddingDTO.getWeddingDate());
        existingWedding.setVenue(weddingDTO.getVenue());
        existingWedding.setEstimatedGuests(weddingDTO.getEstimatedGuests());
        existingWedding.setStatus(weddingDTO.getStatus());
        existingWedding.setNotes(weddingDTO.getNotes());

        weddingRepository.save(existingWedding);
        return toDTO(existingWedding);
    }

    /**
     * Deletes a wedding.
     */
    public void deleteWedding(String id) throws IOException {
        Wedding wedding = weddingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wedding not found with id: " + id));
        weddingRepository.delete(id);
    }

    /**
     * Cancels a wedding.
     */
    public void cancelWedding(String id) throws IOException {
        Wedding wedding = weddingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wedding not found with id: " + id));
        wedding.cancel();
        weddingRepository.save(wedding);
    }

    /**
     * Gets wedding count.
     */
    public long getWeddingCount() throws IOException {
        return weddingRepository.count();
    }

    /**
     * Gets upcoming wedding count.
     */
    public long getUpcomingWeddingCount() throws IOException {
        return weddingRepository.countUpcoming();
    }

    /**
     * Converts DTO to Entity.
     */
    private Wedding toEntity(WeddingDTO dto) {
        Wedding wedding = new Wedding(dto.getId());
        wedding.setCustomerId(dto.getCustomerId());
        wedding.setBrideName(dto.getBrideName());
        wedding.setGroomName(dto.getGroomName());
        wedding.setWeddingDate(dto.getWeddingDate());
        wedding.setVenue(dto.getVenue());
        wedding.setEstimatedGuests(dto.getEstimatedGuests());
        wedding.setStatus(dto.getStatus() != null ? dto.getStatus() : Wedding.WeddingStatus.PLANNING);
        wedding.setNotes(dto.getNotes());
        return wedding;
    }

    /**
     * Converts Entity to DTO.
     */
    private WeddingDTO toDTO(Wedding wedding) {
        return new WeddingDTO(
                wedding.getId(),
                wedding.getCustomerId(),
                wedding.getBrideName(),
                wedding.getGroomName(),
                wedding.getWeddingDate(),
                wedding.getVenue(),
                wedding.getEstimatedGuests(),
                wedding.getStatus(),
                wedding.getNotes()
        );
    }
}
