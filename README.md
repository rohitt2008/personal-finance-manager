# Personal Finance Manager

A comprehensive personal finance management REST API built with **Spring Boot 3.x**, enabling users to track income, expenses, savings goals, and generate financial reports.

## Tech Stack

| Component | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.14 |
| Security | Spring Security (Session-based) |
| Database | H2 (In-memory) |
| ORM | Spring Data JPA / Hibernate |
| Build Tool | Maven |
| Testing | JUnit 5, MockMvc |

## Features

- **User Authentication** — Registration, session-based login/logout with secure cookies
- **Transaction Management** — Full CRUD with date/category filtering, auto-type detection from category
- **Category Management** — 7 default categories + user-defined custom categories
- **Savings Goals** — Create goals with dynamic progress tracking computed from actual transactions
- **Reports** — Monthly and yearly reports with income/expenses broken down by category
- **Data Isolation** — Complete segregation between user accounts
- **Input Validation** — Comprehensive validation with proper HTTP status codes (400, 401, 403, 404, 409)

## Project Architecture

```
Controller → Service → Repository → Database
     ↕           ↕
   DTOs    Exception Handler
```

- **Layered Architecture**: Controller → Service → Repository
- **DTOs**: Separate request/response objects from entities
- **Exception Handling**: Global exception handler with `@RestControllerAdvice`
- **Data Isolation**: All entities linked to users via `@ManyToOne` relationships

## API Endpoints

### Authentication
| Method | Endpoint | Status Codes |
|--------|----------|-------------|
| POST | `/api/auth/register` | 201, 400, 409 |
| POST | `/api/auth/login` | 200, 401 |
| POST | `/api/auth/logout` | 200, 401 |

### Transactions
| Method | Endpoint | Status Codes |
|--------|----------|-------------|
| POST | `/api/transactions` | 201, 400, 401 |
| GET | `/api/transactions` | 200, 401 |
| PUT | `/api/transactions/{id}` | 200, 400, 401, 404 |
| DELETE | `/api/transactions/{id}` | 200, 401, 404 |

### Categories
| Method | Endpoint | Status Codes |
|--------|----------|-------------|
| GET | `/api/categories` | 200, 401 |
| POST | `/api/categories` | 201, 400, 401, 409 |
| DELETE | `/api/categories/{name}` | 200, 400, 401, 403, 404 |

### Savings Goals
| Method | Endpoint | Status Codes |
|--------|----------|-------------|
| POST | `/api/goals` | 201, 400, 401 |
| GET | `/api/goals` | 200, 401 |
| GET | `/api/goals/{id}` | 200, 401, 404 |
| PUT | `/api/goals/{id}` | 200, 400, 401, 404 |
| DELETE | `/api/goals/{id}` | 200, 401, 404 |

### Reports
| Method | Endpoint | Status Codes |
|--------|----------|-------------|
| GET | `/api/reports/monthly/{year}/{month}` | 200, 401 |
| GET | `/api/reports/yearly/{year}` | 200, 401 |

## Setup & Run

### Prerequisites
- Java 17+
- Maven 3.8+

### Build & Run
```bash
# Clone the repository
git clone <repository-url>
cd personal-finance-manager

# Build
./mvnw clean compile

# Run
./mvnw spring-boot:run

# The API will be available at http://localhost:8080/api
```

### Run Tests
```bash
./mvnw test
```

## Default Categories

| Category | Type |
|----------|------|
| Salary | INCOME |
| Food | EXPENSE |
| Rent | EXPENSE |
| Transportation | EXPENSE |
| Entertainment | EXPENSE |
| Healthcare | EXPENSE |
| Utilities | EXPENSE |

## Design Decisions

1. **Session-based Auth**: Uses HTTP session cookies for authentication, as specified in the requirements. No JWT tokens.
2. **BCrypt Password Encoding**: Passwords are hashed using BCrypt for security.
3. **In-memory H2 Database**: Simplifies deployment and testing. Data resets on restart.
4. **Dynamic Goal Progress**: Progress is computed on-the-fly from actual transactions since the goal's start date, rather than being stored statically.
5. **Category-Transaction Relationship**: Transactions reference categories by name for flexibility. The transaction type (INCOME/EXPENSE) is automatically derived from the category type.
6. **Data Isolation**: All entities (transactions, custom categories, goals) are linked to users via `@ManyToOne` relationships. Default categories are shared (user=null).
