# Lab 7: Advanced Collection Manager with Database Integration and Concurrency

This repository contains the source code for the 7th laboratory assignment of the "Programming" course at ITMO University. This project is a sophisticated, multi-threaded, and multi-user client-server application engineered to manage a collection of `Ticket` objects. It evolves the architecture of Lab 6 by replacing file-based persistence with a robust PostgreSQL database, implementing a secure user authentication and authorization system, and managing concurrent operations with advanced Java concurrency utilities.

---

## Table of Contents
- [Project Variant](#project-variant)
- [Core Features](#core-features)
- [System Architecture](#system-architecture)
  - [Server-Side Architecture](#1-server-side-architecture)
  - [Client-Side Architecture](#2-client-side-architecture)
  - [Common Module](#3-common-module)
- [Database Schema](#database-schema)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Setup](#setup)

---

## Project Variant

- **Collection Type:** `java.util.LinkedHashMap<Long, Ticket>`
- **Managed Object:** `Ticket`
- **Database System:** PostgreSQL
- **Password Hashing Algorithm:** SHA-384

---

## Core Features

- **Relational Database Persistence:** The collection is stored in a PostgreSQL database, ensuring data integrity and durability. The legacy file-based storage has been completely removed.
- **User Authentication & Authorization:** A complete user management system that includes:
  - Secure user registration with **SHA-384 password hashing**.
  - A mandatory login flow before any commands can be executed.
  - Ownership-based permissions: users can view all objects but can only modify or delete objects they have created.
- **Concurrent Request Handling:** The server is fully multi-threaded to handle multiple clients simultaneously without blocking.
- **Thread-Safe Collection Management:** The in-memory collection is protected against race conditions and data corruption using `java.util.concurrent.locks.ReentrantReadWriteLock`.
- **Object-Oriented Design:** The project adheres to SOLID principles, utilizing design patterns like **Command**, **Data Access Object (DAO)**, and **Data Transfer Object (DTO)** for a clean, modular, and scalable architecture.
- **UDP-Based Communication:** A custom request-response protocol is implemented over UDP for network communication between the client and server.

---

## System Architecture

The application follows a classic **client-server model** with a clear separation of concerns.

### 1. Server-Side Architecture

The server is the authoritative core of the application, handling all business logic, data persistence, and security.

#### a. Network Layer (`UDPServer`)
- **Protocol:** Communication is handled via a stateless UDP protocol.
- **Multi-threading Model:**
  - **Request Processing Pool:** An `Executors.newCachedThreadPool()` is used to process incoming requests. This allows the server to dynamically scale the number of active threads based on load, ensuring high responsiveness for many short-lived tasks.
  - **Response Sending Pool:** A separate `Executors.newFixedThreadPool()` manages the sending of responses. This decouples the I/O-bound task of sending data from the CPU-bound task of command processing, preventing network latency from impacting the server's ability to process new requests.

#### b. Persistence Layer (PostgreSQL & DAO)
- **Database:** PostgreSQL serves as the single source of truth for all `Ticket` and `User` data. IDs for tickets are generated automatically using a database `SEQUENCE`.
- **Data Access Object (DAO) Pattern:** The interaction with the database is abstracted through a dedicated DAO layer.
  - `UserDAO`: Encapsulates all SQL queries related to the `users` table, including user creation and credential verification.
  - `TicketDAO`: Manages all CRUD (Create, Read, Update, Delete) operations for the `tickets` table.
- **In-Memory & DB Synchronization:**
  - On startup, the server loads the entire `tickets` collection from the database into an in-memory `LinkedHashMap` for fast read access.
  - All write operations (`insert`, `update`, `delete`) are transactional: they are first executed on the database. Only upon a successful database commit is the in-memory collection updated. This ensures consistency between the in-memory cache and the persistent storage.

#### c. Concurrency and Synchronization (`CollectionManager`)
- **`ReentrantReadWriteLock`:** This advanced locking mechanism is employed to protect the in-memory collection from concurrent access issues.
  - **Read Lock:** Allows multiple threads to read from the collection simultaneously, maximizing throughput for read-heavy commands like `show` and `info`.
  - **Write Lock:** Provides exclusive access for threads performing modifications (`insert`, `update`, `remove`). It blocks all other read and write threads until the operation is complete, guaranteeing data integrity.

#### d. Security Layer
- **Authentication:** The `CommandProcessor` acts as a security gateway. It intercepts every incoming request (except `register`) to verify the user's credentials against the database via the `UserDAO` before dispatching the command for execution.
- **Authorization:** For commands that modify data, an additional authorization check is performed. The command logic verifies that the `user_id` of the user making the request matches the `owner_id` of the ticket being modified.
- **Password Hashing:** Passwords are never stored in plaintext. The `PasswordHasher` utility uses the cryptographically strong **SHA-384** algorithm to generate a one-way hash of the user's password, which is then stored in the database.

### 2. Client-Side Architecture

The client is a lightweight console application focused on user interaction.

- **User State Management (`Runner`):** The main `Runner` class manages the user's session. It enforces an authentication flow on startup, requiring the user to either `login` or `register`.
- **Request Generation:** Once authenticated, the client attaches the `User` object (containing username and password) to every `Request` DTO sent to the server.
- **Command Pattern (`ICommand`):** Client-side commands are responsible for parsing user input and building the data portion of a `Request` DTO. For commands requiring complex input (like `insert`), they utilize `Form` classes to guide the user through data entry.

### 3. Common Module

A shared library containing code used by both the client and the server, ensuring consistency and reducing code duplication.
- **Data Transfer Objects (DTOs):** `Request` and `Response` classes define the standardized protocol for all client-server communication.
- **Data Models:** Contains the `Serializable` classes representing the core entities of the application (`Ticket`, `User`, `Coordinates`, `Event`, etc.).
- **Custom Exceptions:** A set of specific exceptions for handling application-level errors, such as `InvalidFormException` or `WrongAmountOfElementsException`.

---

## Database Schema

The PostgreSQL database consists of two tables and one sequence.

```sql
-- Sequence for auto-generating unique ticket IDs
CREATE SEQUENCE tickets_id_seq START WITH 1 INCREMENT BY 1;

-- Stores user credentials with hashed passwords
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL
);

-- Stores all ticket data and links each ticket to its owner
CREATE TABLE tickets (
    id INT PRIMARY KEY DEFAULT nextval('tickets_id_seq'),
    obj_key BIGINT UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    coordinates_x FLOAT NOT NULL,
    coordinates_y FLOAT NOT NULL,
    creation_date DATE NOT NULL,
    price INT NOT NULL CHECK (price > 0),
    discount BIGINT NOT NULL CHECK (discount > 0 AND discount <= 100),
    comment VARCHAR(631),
    ticket_type VARCHAR(50) NOT NULL,
    event_name VARCHAR(255),
    event_date TIMESTAMP WITH TIME ZONE,
    event_type VARCHAR(50),
    user_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or higher.
- Apache Maven (for dependency management, optional).
- PostgreSQL Server.
- PostgreSQL JDBC Driver (`.jar` file).

### Setup

1.  **Clone the repository:**
    ```sh
    git clone <repository-url>
    ```
2.  **Configure the Database:**
    - Connect to your PostgreSQL server and create a database (e.g., `studs`).
    - Execute the SQL script provided in the [Database Schema](#database-schema) section to create the necessary tables and sequence.
3.  **Add JDBC Driver:**
    - Download the PostgreSQL JDBC Driver `.jar` file.
    - Place it in the `libs` directory of the project.
4.  **Update Database Credentials:**
    - Open `src/server/db/DatabaseManager.java`.
    - Update the `USER` and `PASS` constants with your PostgreSQL credentials.
5.  **Compile and Run:**
    - Compile the entire project, ensuring the JDBC driver is in the classpath.
    - Run the server first, then run one or more client instances.
