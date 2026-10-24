# WEDORA - Wedding Business Management System

A premium, fully functional wedding business management web application built with Java 21, Spring Boot, and Thymeleaf.

## Features

### Core Modules
- **Customer Management**: Track client information with TXT file persistence
- **Vendor Management**: Manage service providers and categories
- **Wedding Event Management**: Create and manage wedding events with status tracking
- **Booking Management**: Book vendors for weddings with conflict validation (2-hour window)
- **Finance Management**: Track expenses and budgets with BigDecimal precision
- **Task Management**: Manage wedding-related tasks with priority and overdue detection
- **Staff Management**: Manage business employees with department and salary tracking
- **Secure OTP Authentication**: 6-digit OTP with 10-minute expiry for secure login

### Key Features
- **TXT File Persistence**: All data stored in user home directory (`~/wedora_data/`)
- **OOP Principles**: Encapsulation, inheritance, abstraction, information hiding
- **Validation**: Jakarta validation annotations on DTOs
- **Exception Handling**: Custom exceptions and global error handler
- **Premium UI**: Modern, responsive design with WEDORA brand colors
- **Live Dashboard**: Real-time statistics and activity feeds

## Technology Stack

- **Java**: 21
- **Spring Boot**: 4.1.1
- **Spring Security**: 7.1.1
- **Thymeleaf**: Server-side templating
- **Maven**: Build tool
- **No Database**: TXT file-based persistence (university version)

## Project Structure

```
src/main/java/com/wedora/
├── controller/        # REST controllers
├── dto/              # Data transfer objects
├── exception/        # Custom exceptions
├── model/            # Domain entities
├── repository/       # Data access layer (TXT files)
├── service/          # Business logic layer
├── security/         # Security configuration
├── util/             # Utility classes
└── WedoraApplication.java
```

## Getting Started

### Prerequisites
- Java 21 or higher
- Maven 3.6+

### Installation

1. Clone the repository:
```bash
git clone https://github.com/MohammedFaizalMohammedAsadhHasan/WEDORA.git
cd WEDORA
```

2. Build the project:
```bash
.\mvnw.cmd clean install
```

3. Run the application:
```bash
.\mvnw.cmd spring-boot:run
```

4. Access the application:
```
http://localhost:8080
```

### Login
- Enter any email address (e.g., `user@example.com`)
- Enter any password
- A 6-digit OTP will be displayed (development mode)
- Enter the OTP to access the dashboard

## Data Storage

All data is stored in TXT files in the user's home directory:
- `~/wedora_data/customers.txt`
- `~/wedora_data/vendors.txt`
- `~/wedora_data/weddings.txt`
- `~/wedora_data/bookings.txt`
- `~/wedora_data/expenses.txt`
- `~/wedora_data/tasks.txt`
- `~/wedora_data/staff.txt`
- `~/wedora_data/otp_tokens.txt`

## OOP Concepts Demonstrated

1. **Encapsulation**: Private fields with controlled access through getters/setters
2. **Inheritance**: All entities extend `BaseEntity` for common fields
3. **Abstraction**: `BaseEntity` is an abstract class defining common behavior
4. **Polymorphism**: Service interfaces with multiple implementations
5. **Information Hiding**: Repository layer hides file operations from service layer

## Security

- Spring Security with form-based login
- OTP-based two-factor authentication
- Session management
- CSRF protection

## Future Enhancements

- Database integration (PostgreSQL/MySQL)
- Email/SMS OTP delivery
- Role-based access control
- Advanced reporting and analytics
- Payment gateway integration
- Mobile application

## License

This project is for educational purposes.

## Author

Mohammed Faizal Mohammed Asadh Hasan
