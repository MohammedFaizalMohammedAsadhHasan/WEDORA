package com.wedora.service;

import com.wedora.dto.CustomerDTO;
import com.wedora.exception.ResourceNotFoundException;
import com.wedora.model.Customer;
import com.wedora.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Customer business logic.
 * Demonstrates OOP concept: Information Hiding
 * Business rules are encapsulated here, not exposed to controllers.
 */
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Gets all customers.
     */
    public List<CustomerDTO> getAllCustomers() throws IOException {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets active customers only.
     */
    public List<CustomerDTO> getActiveCustomers() throws IOException {
        List<Customer> customers = customerRepository.findActive();
        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Gets customer by ID.
     */
    public CustomerDTO getCustomerById(String id) throws IOException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return toDTO(customer);
    }

    /**
     * Searches customers by name.
     */
    public List<CustomerDTO> searchByName(String searchTerm) throws IOException {
        List<Customer> customers = customerRepository.searchByName(searchTerm);
        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Searches customers by contact (phone or email).
     */
    public List<CustomerDTO> searchByContact(String searchTerm) throws IOException {
        List<Customer> customers = customerRepository.searchByContact(searchTerm);
        return customers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Creates a new customer.
     */
    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws IOException {
        // Validate business rules
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty()) {
            // Check for duplicate email
            List<Customer> existing = customerRepository.findAll();
            boolean emailExists = existing.stream()
                    .anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(customerDTO.getEmail()));
            if (emailExists) {
                throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
            }
        }

        Customer customer = toEntity(customerDTO);
        customerRepository.save(customer);
        return toDTO(customer);
    }

    /**
     * Updates an existing customer.
     */
    public CustomerDTO updateCustomer(String id, CustomerDTO customerDTO) throws IOException {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        // Validate email uniqueness if changed
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().equals(existingCustomer.getEmail())) {
            List<Customer> allCustomers = customerRepository.findAll();
            boolean emailExists = allCustomers.stream()
                    .filter(c -> !c.getId().equals(id))
                    .anyMatch(c -> c.getEmail() != null && c.getEmail().equalsIgnoreCase(customerDTO.getEmail()));
            if (emailExists) {
                throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
            }
        }

        // Update fields
        existingCustomer.setFullName(customerDTO.getFullName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhone(customerDTO.getPhone());
        existingCustomer.setAddress(customerDTO.getAddress());
        existingCustomer.setNotes(customerDTO.getNotes());
        existingCustomer.setActive(customerDTO.isActive());

        customerRepository.save(existingCustomer);
        return toDTO(existingCustomer);
    }

    /**
     * Archives (soft deletes) a customer.
     */
    public void deleteCustomer(String id) throws IOException {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        customerRepository.delete(id);
    }

    /**
     * Gets customer count.
     */
    public long getCustomerCount() throws IOException {
        return customerRepository.count();
    }

    /**
     * Gets active customer count.
     */
    public long getActiveCustomerCount() throws IOException {
        return customerRepository.countActive();
    }

    /**
     * Converts DTO to Entity.
     */
    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer(dto.getId());
        customer.setFullName(dto.getFullName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setNotes(dto.getNotes());
        customer.setActive(dto.isActive());
        return customer;
    }

    /**
     * Converts Entity to DTO.
     */
    private CustomerDTO toDTO(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getNotes(),
                customer.isActive()
        );
    }
}
