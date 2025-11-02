# Database Lab 1: Relational Model of the City of Diaspar

This repository contains the solution for the first laboratory assignment in the "Databases" course. The project involves analyzing a textual description of a fictional world (the city of Diaspar), designing a comprehensive database model based on it, and implementing this model in PostgreSQL. The process covers conceptual (infological) modeling, logical (datalogical) modeling, and physical implementation with SQL, including advanced integrity constraints.

## Table of Contents
- [Assignment Description](#assignment-description)
- [Subject Area Analysis (Variant 408078)](#subject-area-analysis-variant-408078)
- [Database Design](#database-design)
  - [1. Entity Identification](#1-entity-identification)
  - [2. Conceptual Model (Infological)](#2-conceptual-model-infological)
  - [3. Logical Model (Datalogical)](#3-logical-model-datalogical)
- [SQL Implementation Details](#sql-implementation-details)
  - [Custom Data Types (`ENUM`)](#custom-data-types-enum)
  - [Integrity Constraints](#integrity-constraints)
  - [Advanced Business Logic (Triggers)](#advanced-business-logic-triggers)
- [Database Schema (SQL DDL)](#database-schema-sql-ddl)
- [Sample Data (SQL DML)](#sample-data-sql-dml)
- [How to Set Up and Run](#how-to-set-up-and-run)
- [Conclusion](#conclusion)

## Assignment Description

The core tasks for this laboratory assignment were:
1.  **Analyze the provided text** to create a detailed description of the subject area. Identify the main entities, their attributes, and the relationships between them.
2.  **Develop a conceptual (infological) model** to represent these entities and relationships at a high level.
3.  **Create a logical (datalogical) model** based on the conceptual model, defining tables, columns, and data types specific to PostgreSQL.
4.  **Implement the logical model in PostgreSQL**, ensuring that all relevant data integrity constraints (primary keys, foreign keys, checks, etc.) are enforced.
5.  **Populate the created tables** with sample test data to demonstrate the model's functionality.

## Subject Area Analysis (Variant 408078)

The database model is based on the following description of the city of Diaspar:

> *In the city of Diaspar, which can host multiple zones, every zone is protected by a single security system that cannot exist without its zone, and that system in turn manages multiple doors or barriers while always including exactly one voice interface. Characters in the city live independently and may or may not interact with any door or barrier; when they do, their attempts are logged through access records, each of which can spawn multiple events describing what happened. A character can also try to escape from a zone, creating escape attempts linked to that zone, though a zone itself can have zero or many such attempts. Lastly, missions in Diaspar represent grand objectives that characters can optionally join via mission participation, meaning a mission can involve many characters or none, and each character can choose to take part in many missions or none at all.*

From this text, we identified key nouns as potential entities and verbs as potential relationships, forming the foundation of the database design.

## Database Design

The design process was broken down into three systematic stages.

### 1. Entity Identification

Entities were categorized based on their role in the model:

-   **Core Entities (Independent):**
    -   `City`: The central entity representing Diaspar itself.
    -   `Character`: Individuals living within the city.
    -   `Mission`: High-level objectives available to characters.
-   **Characteristic Entities (Dependent on Core Entities):**
    -   `Zone`: An area within the `City`.
    -   `SecuritySystem`: Protects a `Zone`.
    -   `Door/Barrier`: Controlled by a `SecuritySystem`.
    -   `VoiceInterface`: Part of a `SecuritySystem`.
    -   `Event`: Describes a detail of an `AccessRecord`.
    -   `EscapeAttempt`: An action performed by a `Character` related to a `Zone`.
-   **Associative Entities (Junction/Linking Tables):**
    -   `AccessRecord`: Links a `Character` to a `Door` interaction.
    -   `MissionParticipation`: Links `Character`s to `Mission`s, resolving a many-to-many relationship.

### 2. Conceptual Model (Infological)

This high-level model defines the relationships between entities:
-   A `City` has a one-to-many relationship with `Zone`.
-   A `Zone` has a one-to-one relationship with a `SecuritySystem` (a system cannot exist without its zone).
-   A `SecuritySystem` has a one-to-many relationship with `Door` and a mandatory one-to-one relationship with `VoiceInterface`.
-   A `Character` and a `Door` have a many-to-many relationship, resolved by the `AccessRecord` entity.
-   An `AccessRecord` has a one-to-many relationship with `Event`.
-   A `Character` and a `Zone` have a many-to-many relationship regarding escape attempts, modeled as a one-to-many relationship from both `Character` and `Zone` to `EscapeAttempt`.
-   A `Character` and a `Mission` have a many-to-many relationship, resolved by the `MissionParticipation` associative entity.

### 3. Logical Model (Datalogical)

The conceptual model was translated into a physical PostgreSQL schema. Primary Keys (PK) and Foreign Keys (FK) were defined to enforce relational integrity. Many-to-many relationships were implemented using junction tables.

## SQL Implementation Details

Several PostgreSQL features were used to ensure data integrity and model the domain accurately.

### Custom Data Types (`ENUM`)

To enforce a controlled set of values for certain attributes, custom `ENUM` types were created. This improves data consistency and readability.
-   `outcome`: ('Success', 'Failure')
-   `zone_type`: ('Forbidden', 'Residental', 'Industrial')
-   `system_type`: ('Surveillance', 'Access', 'Control')
-   And many others for status, roles, types, etc.

### Integrity Constraints

The schema heavily relies on constraints to maintain data validity:
-   **`PRIMARY KEY`**: Each table has a `SERIAL` primary key for unique identification.
-   **`FOREIGN KEY`**: Relationships between tables are enforced using foreign keys. `ON DELETE CASCADE` is used where appropriate (e.g., if a `Zone` is deleted, its associated `SecuritySystem` is also deleted).
-   **`NOT NULL`**: Ensures that mandatory attributes contain a value.
-   **`CHECK`**: Enforces business rules at the database level (e.g., `population >= 0`, `age BETWEEN 0 AND 100`, `end_date > start_date`).
-   **`DEFAULT`**: Provides default values for certain columns (e.g., `name` for `City` defaults to 'Diaspar').

### Advanced Business Logic (Triggers)

A trigger was implemented to enforce a complex, cross-table business rule that cannot be handled by a simple `CHECK` constraint.
-   **`trg_check_installation_date`**: This `BEFORE INSERT OR UPDATE` trigger on the `SecuritySystem` table executes a function `check_installation_date()`. The function verifies that a security system's `installation_date` is not earlier than the `founding_date` of the city it belongs to. This ensures logical consistency across the entire database.

## Database Schema (SQL DDL)

<details>
<summary>Click to view the full SQL script for creating the database schema.</summary>

```sql
-- Custom ENUM Types
CREATE TYPE outcome AS ENUM ('Success', 'Failure');
CREATE TYPE zone_type AS ENUM ('Forbidden', 'Residental', 'Industrial');
CREATE TYPE system_type AS ENUM ('Surveillance', 'Access', 'Control');
CREATE TYPE active_status AS ENUM ('Active', 'Disable');
CREATE TYPE door_type AS ENUM ('Sliding', 'Locking');
CREATE TYPE door_status AS ENUM ('Open', 'Closed', 'Locked', 'Blocked');
CREATE TYPE voice_type AS ENUM ('Male', 'Female','Robotic');
CREATE TYPE role AS ENUM ('Scout', 'Leader');
CREATE TYPE character_status AS ENUM ('Dead', 'Captured','Alive');
CREATE TYPE access_status AS ENUM ('Garanted', 'Denied');
CREATE TYPE event_Type AS ENUM ('Door Opened', 'Security Alert');
CREATE TYPE mission_status AS ENUM ('In Progress', 'Completed');
CREATE TYPE assigned_role AS ENUM ('Recon', 'Support');
CREATE TYPE contribution AS ENUM ('Strategy', 'Combat');

-- Table Creation
CREATE TABLE City (
    city_id SERIAL PRIMARY KEY,
    name VARCHAR(31) NOT NULL DEFAULT 'Diaspar',
    population INT CHECK (population >=0),
    founding_date DATE CHECK (founding_date <= CURRENT_DATE)
);

CREATE TABLE Character (
    character_id SERIAL PRIMARY KEY,
    name VARCHAR(31) NOT NULL,
    role role NOT NULL,
    age INT CHECK (age BETWEEN 0 AND 100),
    skills TEXT,
    status character_status NOT NULL
);

CREATE TABLE Zone (
    zone_id SERIAL PRIMARY KEY,
    city_id INT REFERENCES city(city_id) ON DELETE CASCADE NOT NULL,
    name VARCHAR(31) NOT NULL,
    zone_type zone_type NOT NULL,
    security_level INT,
    description TEXT
);

CREATE TABLE SecuritySystem (
    system_id SERIAL PRIMARY KEY,
    zone_id INT REFERENCES zone(zone_id) ON DELETE CASCADE NOT NULL,
    city_id INT REFERENCES city(city_id) ON DELETE CASCADE NOT NULL,
    security_type system_type NOT NULL,
    status active_status NOT NULL DEFAULT 'Active',
    installation_date DATE NOT NULL
);

-- Trigger Function and Trigger
CREATE OR REPLACE FUNCTION check_installation_date()
RETURNS TRIGGER AS $$
DECLARE
    city_founding_date DATE;
BEGIN
    SELECT founding_date INTO city_founding_date
    FROM city
    WHERE city_id = NEW.city_id;

    IF city_founding_date IS NULL THEN
        RAISE EXCEPTION 'No such city_id: %', NEW.city_id;
    END IF;

    IF NEW.installation_date < city_founding_date THEN
        RAISE EXCEPTION 'installation_date (%) must be on or after city founding_date (%)',
                        NEW.installation_date, city_founding_date;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_installation_date
BEFORE INSERT OR UPDATE ON SecuritySystem
FOR EACH ROW
EXECUTE PROCEDURE check_installation_date();

CREATE TABLE Door (
    Door_ID SERIAL PRIMARY KEY,
    System_ID INT REFERENCES SecuritySystem(System_ID) ON DELETE CASCADE NOT NULL,
    Door_Type Door_Type,
    Access_Code VARCHAR(255),
    Status Door_Status
);

CREATE TABLE AccessRecord (
    Access_ID SERIAL PRIMARY KEY,
    Character_ID INT REFERENCES Character(Character_ID) ON DELETE CASCADE,
    Door_ID INT REFERENCES Door(Door_ID) ON DELETE CASCADE,
    Access_Time TIMESTAMP NOT NULL DEFAULT now(),
    Access_Status Access_Status
);

CREATE TABLE Event (
    Event_ID SERIAL PRIMARY KEY,
    Access_ID INT REFERENCES AccessRecord(Access_ID) ON DELETE CASCADE,
    Event_Type Event_Type NOT NULL,
    Timestamp TIMESTAMP NOT NULL DEFAULT now(),
    Details TEXT
);

CREATE TABLE EscapeAttempt (
    Attempt_ID SERIAL PRIMARY KEY,
    Character_ID INT REFERENCES Character(Character_ID) ON DELETE CASCADE NOT NULL,
    Zone_ID INT REFERENCES Zone(Zone_ID) ON DELETE CASCADE NOT NULL,
    Attempt_Time TIMESTAMP NOT NULL DEFAULT now(),
    Outcome Outcome
);

CREATE TABLE Mission (
    mission_id SERIAL PRIMARY KEY,
    name VARCHAR(31) NOT NULL,
    objective TEXT,
    start_date DATE,
    end_date DATE CHECK (end_date > start_date),
    status mission_status
);

CREATE TABLE MissionParticipation (
    Participation_ID SERIAL PRIMARY KEY,
    Character_ID INT REFERENCES Character(Character_ID) ON DELETE CASCADE NOT NULL,
    Mission_ID INT REFERENCES Mission(Mission_ID) ON DELETE CASCADE NOT NULL,
    Assigned_Role Assigned_Role,
    Contribution Contribution
);

CREATE TABLE VoiceInterface (
    Interface_ID SERIAL PRIMARY KEY,
    System_ID INT REFERENCES SecuritySystem(System_ID) ON DELETE CASCADE NOT NULL,
    Voice_Type Voice_Type,
    Language VARCHAR(63),
    Response_Time INT
);
```
</details>

## Sample Data (SQL DML)

<details>
<summary>Click to view the SQL script for populating the database with test data.</summary>

```sql
INSERT INTO City (City_ID, Name, Population, Founding_Date)
VALUES (1, 'Diaspar', 100000, '2000-01-01');

INSERT INTO Character (Character_ID, Name, Role, Age, Skills, Status)
VALUES
(1, 'Hedron', 'Leader', 40, 'Leadership, Strategy', 'Alive'),
(2, 'Astra', 'Scout', 28, 'Stealth, Navigation', 'Alive'),
(3, 'Finn', 'Scout', 32, 'Agility, Observation', 'Alive');

INSERT INTO Zone (Zone_ID, City_ID, Name, Zone_Type, Security_Level, Description)
VALUES
(1, 1, 'Forbidden Zone', 'Forbidden', 10, 'A dangerous and restricted area beyond Diaspar''s boundaries.');

INSERT INTO SecuritySystem (System_ID, Zone_ID, city_id, Type, Status, Installation_Date)
VALUES
(1, 1, 1, 'Control', 'Active', '2010-06-15'); -- Note: Date is after city founding

INSERT INTO Door (Door_ID, System_ID, Door_Type, Access_Code, Status)
VALUES
(1, 1, 'Locking', 'XYZ123', 'Closed');

INSERT INTO VoiceInterface (Interface_ID, System_ID, Voice_Type, Language, Response_Time)
VALUES
(1, 1, 'Robotic', 'English', 3);

INSERT INTO AccessRecord (Access_ID, Character_ID, Door_ID, Access_Time, Access_Status)
VALUES
(1, 1, 1, now(), 'Garanted');

INSERT INTO Event (Event_ID, Access_ID, Event_Type, Timestamp, Details)
VALUES
(1, 1, 'Security Alert', now(), 'Alert! Security Breach');

INSERT INTO EscapeAttempt (Attempt_ID, Character_ID, Zone_ID, Attempt_Time, Outcome)
VALUES
(1, 1, 1, now(), 'Success');

INSERT INTO Mission (Mission_ID, Name, Objective, Start_Date, End_Date, Status)
VALUES
(1, 'Escape', 'Uncover the truth beyond Diaspar and flee the sealed city.', CURRENT_DATE, NULL, 'In Progress');

INSERT INTO MissionParticipation (Character_ID, Mission_ID, Assigned_Role, Contribution)
VALUES
(1, 1, 'Recon', 'Strategy'),
(2, 1, 'Support', 'Combat'),
(3, 1, 'Support', 'Combat');
```
</details>

## How to Set Up and Run

1.  **Prerequisites**: Ensure you have a running PostgreSQL server.
2.  **Database Creation**: Connect to your PostgreSQL instance and create a new database.
3.  **Schema Execution**: Execute the SQL script provided in the [Database Schema](#database-schema-sql-ddl) section to create all tables, types, and triggers.
4.  **Data Population**: Execute the SQL script from the [Sample Data](#sample-data-sql-dml) section to populate the database with initial records.
5.  **Verification**: You can now connect to the database using any SQL client (like `psql` or DBeaver) and query the tables.

## Conclusion

Through the completion of this laboratory work, I became familiar with different models of data representation by creating both a conceptual (infological) and a logical (datalogical) model of entities. I learned how to implement logical models for an arbitrary subject area using SQL, including the enforcement of complex data integrity rules through various constraints and triggers.
