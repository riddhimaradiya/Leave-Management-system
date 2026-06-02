# Employee Leave Management System

A REST API backend project built with **Java Spring Boot** for managing employee leave requests.
Employees can apply for leave, managers can approve or reject, and scheduled jobs handle automation.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core language |
| Spring Boot 3 | Backend framework |
| Spring Data JPA + Hibernate | Database ORM |
| Spring Security + JWT | Authentication |
| MySQL | Database |
| Lombok | Reduce boilerplate code |
| Maven | Build tool |
| Postman | API testing |

---

## Features

- Employee registration and login with JWT authentication
- Role-based access — EMPLOYEE / MANAGER / ADMIN
- Apply for Casual, Sick, and Earned leave
- Manager can approve or reject leave requests
- Employee can cancel their own pending leave
- Paginated leave history with sorting
- Leave balance tracking per employee
- Custom exception handling with proper HTTP status codes
- Scheduled jobs running automatically:
    - Auto-cancel pending leaves ignored by manager for 3+ days (runs daily at midnight)
    - Add 1 casual leave to all employees on 1st of every month
    - Daily summary report printed at 8 AM

---

## Project Structure

```
src/main/java/com/example/leave/
├── LeaveManagementApplication.java   # Main entry point
├── controller/
│   ├── AuthController.java           # Register and login APIs
│   ├── LeaveController.java          # Leave apply, approve, reject, cancel APIs
│   └── EmployeeController.java       # Employee list API
├── service/
│   ├── AuthService.java              # Registration and login logic
│   └── LeaveService.java             # Core leave business logic
├── scheduler/
│   └── LeaveSchedulerService.java    # All 3 scheduled cron jobs
├── entity/
│   ├── Employee.java                 # Employee database table
│   ├── LeaveRequest.java             # Leave request database table
│   ├── Role.java                     # Enum: EMPLOYEE, MANAGER, ADMIN
│   ├── LeaveType.java                # Enum: CASUAL, SICK, EARNED
│   └── LeaveStatus.java              # Enum: PENDING, APPROVED, REJECTED, CANCELLED
├── dto/
│   ├── RegisterRequest.java          # Request body for register
│   ├── LoginRequest.java             # Request body for login
│   ├── LeaveApplyRequest.java        # Request body for applying leave
│   └── LeaveResponse.java            # Response body for leave details
├── repository/
│   ├── EmployeeRepository.java       # Employee DB queries
│   └── LeaveRequestRepository.java   # Leave request DB queries
├── security/
│   ├── JwtUtil.java                  # Generate and validate JWT tokens
│   ├── JwtFilter.java                # Intercept and verify JWT on every request
│   └── SecurityConfig.java           # Spring Security configuration
└── exception/
    ├── GlobalExceptionHandler.java    # Catches all exceptions in one place
    ├── InsufficientLeaveException.java
    ├── ResourceNotFoundException.java
    └── UnauthorizedActionException.java
```

---

## How to Run Locally

### Prerequisites
- Java 17 installed
- MySQL installed and running
- Maven installed
- IntelliJ IDEA (recommended)

### Step 1 — Create MySQL database

Open MySQL Workbench and run:
```sql
CREATE DATABASE leave_management_db;
```

### Step 2 — Configure database

Open `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/leave_management_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3 — Run the project

```bash
mvn spring-boot:run
```

Or open the project in IntelliJ IDEA and run `LeaveManagementApplication.java`

Server starts at: `http://localhost:8081`

---

## API Endpoints

### Auth (Public — no token needed)

| Method | URL | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new employee |
| POST | `/api/auth/login` | Login and get JWT token |

### Leave (Protected — JWT token required)

| Method | URL | Description |
|---|---|---|
| POST | `/api/leaves/apply` | Apply for leave |
| PUT | `/api/leaves/{id}/approve` | Approve leave (Manager only) |
| PUT | `/api/leaves/{id}/reject` | Reject leave (Manager only) |
| DELETE | `/api/leaves/{id}/cancel` | Cancel own pending leave |
| GET | `/api/leaves/my?page=0&size=10` | Get paginated leave history |
| GET | `/api/leaves/balance` | Get remaining leave balance |

### Employee

| Method | URL | Description |
|---|---|---|
| GET | `/api/employees?page=0&size=10` | Get all employees (paginated) |

---

## Java Concepts Used

| Concept | Where Used |
|---|---|
| OOP (Inheritance, Encapsulation) | Entity classes, Service layer |
| Exception Handling | Custom exceptions + @ControllerAdvice |
| Multithreading | @Async for non-blocking operations |
| Scheduling | @Scheduled cron jobs in LeaveSchedulerService |
| Pagination | Spring Data Pageable in leave history API |
| JWT Security | JwtUtil, JwtFilter, SecurityConfig |

---

## Default Leave Balance (per employee)

| Leave Type | Days per Year |
|---|---|
| Casual | 12 |
| Sick | 6 |
| Earned | 15 |

---

## Author

**Your Name**
- GitHub: [@yourusername](https://github.com/yourusername)
- LinkedIn: [Your LinkedIn](https://linkedin.com/in/yourprofile)

---

## License

This project is open source and available under the [MIT License](LICENSE).
