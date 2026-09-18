# 🏨 Java Hotel Management System

A console-based **Hotel Management System** developed in Java 17+ with PostgreSQL and JDBC.

The application manages the main operations of a hotel, including **users, rooms, reservations, payments, invoices, dynamic pricing, cancellations, refunds, and reporting**.

The project is built using a **layered architecture**, the **Repository Pattern**, **Singleton Pattern**, and **Strategy Pattern**, with PostgreSQL providing persistent data storage.

> 🎓 This project is designed to apply Java OOP, JDBC, SQL, software architecture, design patterns, and transaction management in a realistic business application.

---

## 📋 Project Context

The initial version of the hotel application stored its data only in memory. As a result, all information was lost when the application restarted.

This version evolves the application into a persistent hotel management system using:

* Java 17+
* PostgreSQL
* JDBC
* Layered Architecture
* Repository Pattern
* Singleton Pattern
* Strategy Pattern
* JDBC Transactions
* Custom Business Exceptions
* Docker for the PostgreSQL environment

The main objective is to build a more realistic, maintainable, and persistent hotel information system.

---

# 🎯 Main Objectives

The system addresses several hotel-management problems:

* 💾 Persistent storage instead of in-memory data
* 🛏️ Room availability management
* 🚫 Prevention of double booking
* 💰 Dynamic pricing
* 📅 Reservation management
* ❌ Cancellation and refund management
* 💳 Payment processing
* 🧾 Invoice generation
* 🔐 Authentication and role-based access
* 📊 Hotel performance reports
* 🔄 Atomic database transactions

---

# 🛠️ Technologies

| Technology    | Purpose                            |
| ------------- | ---------------------------------- |
| ☕ Java 17+    | Application development            |
| 🐘 PostgreSQL | Persistent relational database     |
| 🔌 JDBC       | Java database connectivity         |
| 🐳 Docker     | PostgreSQL development environment |
| 📦 Maven      | Build and dependency management    |
| 💰 BigDecimal | Precise financial calculations     |

---

# 🏗️ Architecture

The application follows a **Layered Architecture** with four main layers.

```text
┌──────────────────────────────────────┐
│       Presentation / CLI Layer       │
│        Menus / Input / Output        │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│          Service Layer               │
│ Business Rules / Validation /        │
│ Pricing / Transactions               │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│        Repository Layer              │
│       Interfaces + JDBC             │
│      PreparedStatement / SQL         │
└──────────────────┬───────────────────┘
                   │
                   ▼
┌──────────────────────────────────────┐
│             PostgreSQL               │
│          Docker Container             │
└──────────────────────────────────────┘
```

### Presentation Layer

Responsible for:

* Console menus
* User input
* Navigation
* Displaying results
* Displaying errors

### Service Layer

Contains the application's business logic:

* Reservation validation
* Room availability
* Pricing
* Cancellation
* Refund calculation
* Authentication
* Transaction orchestration
* Reporting

### Repository Layer

Responsible for:

* SQL queries
* JDBC operations
* CRUD operations
* Database persistence

SQL queries are isolated inside the repository layer.

### Domain Model Layer

Contains:

* Entities
* DTOs
* Enums
* Business objects

---

# 📁 Project Structure

```text
src/
├── Main.java
│
├── config/
│   ├── DatabaseConfig.java
│   └── DatabaseInitializer.java
│
├── db/
│   └── DatabaseConnection.java
│
├── model/
│   ├── User.java
│   ├── Room.java
│   ├── Reservation.java
│   ├── Payment.java
│   ├── Invoice.java
│   └── enums/
│       ├── UserRole.java
│       ├── RoomType.java
│       ├── RoomStatus.java
│       └── ReservationStatus.java
│
├── dto/
│   ├── RoomSearchCriteria.java
│   ├── AvailableRoomDTO.java
│   └── ReservationSummaryDTO.java
│
├── repository/
│   ├── UserRepository.java
│   ├── RoomRepository.java
│   ├── ReservationRepository.java
│   └── jdbc/
│       └── ...
│
├── service/
│   ├── AuthService.java
│   ├── RoomService.java
│   ├── ReservationService.java
│   ├── PricingService.java
│   └── ReportService.java
│
├── policy/
│   ├── PricingStrategy.java
│   └── RefundPolicy.java
│
├── exception/
│   ├── RoomNotAvailableException.java
│   ├── InvalidReservationException.java
│   └── AuthenticationException.java
│
└── util/
    ├── InputUtils.java
    ├── ValidationUtils.java
    ├── DateUtils.java
    └── MoneyUtils.java
```

---

# 👥 User & Authentication Management

The application supports different user roles.

### ADMIN

Can manage:

* Rooms
* Hotel data
* Statistics
* Reports
* Global management operations

### CLIENT

Can:

* Search rooms
* Make reservations
* Cancel reservations
* View reservation history

Access to service operations is controlled according to the authenticated user's role.

---

# 🔐 Password Security

User passwords are not stored directly.

The specification requires password hashing using:

```text
Password
   +
Random Salt
   │
   ▼
SHA-256
   │
   ▼
Stored Password Hash
```

Each user receives a randomly generated salt.

---

# 🛏️ Room Management

The room management module handles:

* Room creation
* Room modification
* Room deletion
* Room search
* Room availability
* Room type
* Room status
* Room pricing

Example room types can include:

```text
SINGLE
DOUBLE
SUITE
```

Room status can include:

```text
AVAILABLE
OCCUPIED
MAINTENANCE
```

---

# 📅 Reservation Management

The reservation system handles:

* Room search
* Availability verification
* Reservation creation
* Reservation history
* Reservation cancellation
* Reservation status
* Pricing calculation

The system must prevent **double booking** by checking overlapping reservations in PostgreSQL.

Conceptually:

```text
Existing Reservation
      │
      ▼
Check date overlap
      │
      ├── Overlap ──────► Room unavailable
      │
      └── No overlap ───► Room available
```

---

# 💰 Dynamic Pricing

The pricing engine calculates the total cost based on the room's base price and several business rules.

### Pricing Rules

| Rule          | Condition                 | Adjustment |
| ------------- | ------------------------- | ---------: |
| High Season   | July / August             |       +30% |
| Low Season    | November → February       |       -15% |
| Weekend       | Friday / Saturday nights  |       +15% |
| Long Stay 1   | ≥ 7 nights                |       -10% |
| Long Stay 2   | ≥ 14 nights               |       -15% |
| Early Booking | ≥ 30 days before check-in |        -5% |
| Last Minute   | ≤ 3 days before check-in  |       +10% |

These rules are implemented through the pricing strategy mechanism.

```text
PricingStrategy
       │
       ▼
PricingService
       │
       ├── Season adjustment
       ├── Weekend adjustment
       ├── Long-stay adjustment
       ├── Early-booking adjustment
       └── Last-minute adjustment
```

---

# ❌ Cancellation & Refunds

When a confirmed reservation is cancelled, the refund amount depends on how far the cancellation is from the check-in date.

| Cancellation Time      | Refund |
| ---------------------- | -----: |
| More than 14 days      |   100% |
| Between 7 and 14 days  |    70% |
| Between 48h and 7 days |    50% |
| Less than 48h          |     0% |

The `RefundPolicy` abstraction allows cancellation rules to be isolated from the main reservation logic.

---

# 💳 Payments & Transactions

Reservation, payment, and invoice operations must remain consistent.

The application uses JDBC transactions to guarantee atomicity.

Conceptually:

```text
BEGIN TRANSACTION
        │
        ├── Create Reservation
        │
        ├── Create Payment
        │
        └── Create Invoice
        │
        ▼
     COMMIT
```

If one operation fails:

```text
BEGIN TRANSACTION
        │
        ├── Create Reservation ✓
        │
        ├── Create Payment ✗
        │
        ▼
      ROLLBACK
```

This prevents partially completed financial operations.

---

# 🧾 Invoicing

Invoices contain the financial information associated with a reservation.

The system calculates:

```text
Subtotal HT
     │
     ▼
TVA 20%
     │
     ▼
Total TTC
```

Financial calculations use Java's `BigDecimal` to avoid floating-point precision issues.

---

# 📊 Reporting & KPIs

The `ReportService` provides management indicators such as:

* Occupancy rate
* Revenue
* Average booking value
* Most requested rooms
* Reservation statistics

Example:

```text
========== HOTEL REPORT ==========

Occupancy Rate       : 78.50%
Total Revenue        : 125000.00 MAD
Average Booking      : 1850.00 MAD
Most Requested Room  : 204

==================================
```

---

# 🧩 Design Patterns

## Repository Pattern

The repository layer isolates database access.

```text
Service
   │
   ▼
Repository Interface
   │
   ▼
JDBC Implementation
   │
   ▼
PostgreSQL
```

This keeps SQL/JDBC code outside the business layer.

---

## Singleton Pattern

`DatabaseConnection` provides centralized access to the JDBC connection.

The specification requires a **thread-safe Singleton using Double-Checked Locking**.

```text
Application
     │
     ▼
DatabaseConnection
     │
     └── Single shared instance
              │
              ▼
         PostgreSQL
```

---

## Strategy Pattern

The Strategy Pattern is used for rules that can change independently.

### Pricing

```text
PricingStrategy
      │
      ├── Seasonal pricing
      ├── Weekend pricing
      └── Other pricing rules
```

### Refund

```text
RefundPolicy
      │
      ├── Cancellation rules
      └── Refund calculation
```

This makes business rules easier to modify and extend.

---

# 🐳 Docker Setup

PostgreSQL is run through Docker to provide a reproducible development environment.

Example:

```text
Java Application
       │
       │ JDBC
       ▼
┌──────────────────┐
│ Docker Container │
│                  │
│   PostgreSQL     │
│      :5432       │
└──────────────────┘
```

## Requirements

Install:

* Java 17+
* Maven
* Docker
* Docker Compose

Verify:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

# 🚀 Installation

### 1. Clone the project

```bash
git clone https://github.com/YOUR_USERNAME/java-hotel-management-system.git

cd java-hotel-management-system
```

### 2. Start PostgreSQL

```bash
docker compose up -d
```

Check the container:

```bash
docker ps
```

### 3. Configure the database

Configure the application with the PostgreSQL connection:

```text
Host: localhost
Port: 5432
Database: hotel_management
Username: postgres
Password: ********
```

Database credentials should **not** be committed to Git.

### 4. Build

```bash
mvn clean install
```

### 5. Run

Start the application from your IDE or through Maven.

```text
Main.java
```

---

# 🔄 Application Flow

A typical reservation workflow looks like:

```text
Login
  │
  ▼
Search Available Rooms
  │
  ▼
Select Room
  │
  ▼
Validate Dates
  │
  ▼
Calculate Dynamic Price
  │
  ▼
Create Reservation
  │
  ▼
Process Payment
  │
  ▼
Generate Invoice
```

---

# 🛡️ Security Practices

The application applies several security principles:

* Password hashing with salt
* Role-based access control
* Prepared statements
* Input validation
* Custom business exceptions
* No SQL string concatenation with user input
* No hardcoded production credentials

---

# 📚 Learning Objectives

This project provides practical experience with:

### Java

* Object-Oriented Programming
* Interfaces
* Enums
* Collections
* Exception handling
* `BigDecimal`
* Date/time API

### JDBC

* Connections
* `PreparedStatement`
* `ResultSet`
* Transactions
* Commit / Rollback

### PostgreSQL

* Relational database design
* SQL queries
* Joins
* Constraints
* Foreign keys
* Date-range queries
* Aggregations

### Software Architecture

* Layered Architecture
* Repository Pattern
* DTOs
* Singleton Pattern
* Strategy Pattern
* Service Layer

### DevOps / Environment

* Docker
* Docker Compose
* Reproducible database environment

---

# 📈 Future Improvements

Potential future improvements:

* [ ] Automated unit tests with JUnit
* [ ] Integration tests
* [ ] Database migrations with Flyway
* [ ] Structured logging
* [ ] More pricing strategies
* [ ] More refund policies
* [ ] Advanced reporting
* [ ] Dockerize the Java application
* [ ] CI/CD with GitHub Actions
* [ ] REST API with Spring Boot
* [ ] Angular frontend

---

# 👨‍💻 Author

**Hamza Annane**

Java / Angular Student 🇲🇦

---

## 📌 Project Status

🚧 **In Development**

This project is being developed as a Java SE hotel management application with PostgreSQL persistence and a focus on clean architecture, design patterns, transaction management, and realistic business rules.
