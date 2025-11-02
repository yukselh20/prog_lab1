# Database Lab 3: Normalization, Denormalization, and PL/pgSQL

This repository documents the work for the third laboratory assignment in the "Databases" course. This assignment builds upon the relational model created in Lab 1. The primary focus is on database theory and practice, including the analysis of functional dependencies, normalization to higher forms (3NF and BCNF), the strategic application of denormalization, and the implementation of procedural logic within the database using PL/pgSQL triggers.

## Table of Contents
- [Assignment Description](#assignment-description)
- [Functional Dependency (FD) Analysis](#functional-dependency-fd-analysis)
- [Normalization Analysis](#normalization-analysis)
  - [First Normal Form (1NF)](#first-normal-form-1nf)
  - [Second Normal Form (2NF)](#second-normal-form-2nf)
  - [Third Normal Form (3NF)](#third-normal-form-3nf)
  - [Boyce-Codd Normal Form (BCNF)](#boyce-codd-normal-form-bcnf)
- [Denormalization Strategies](#denormalization-strategies)
  - [Strategy 1: Merging One-to-One Related Tables](#strategy-1-merging-one-to-one-related-tables)
  - [Strategy 2: Adding a Redundant Attribute for Performance](#strategy-2-adding-a-redundant-attribute-for-performance)
- [PL/pgSQL Trigger Implementation](#plpgsql-trigger-implementation)
  - [Objective](#objective)
  - [PL/pgSQL Function: `log_denied_access_event()`](#plpgsql-function-log_denied_access_event)
  - [Trigger Definition: `trg_log_denied_access`](#trigger-definition-trg_log_denied_access)
- [Conclusion](#conclusion)

## Assignment Description

For the relational schema designed in Lab 1, the following tasks were required:
1.  **Describe Functional Dependencies:** Identify and list the minimal set of functional dependencies for the relations in the schema.
2.  **Normalize to 3NF:** Convert the relations to at least the Third Normal Form (3NF) and describe the changes made.
3.  **Normalize to BCNF:** Convert the relations to Boyce-Codd Normal Form (BCNF) and provide proof that the final schema satisfies BCNF.
4.  **Analyze Denormalization:** Propose and describe beneficial denormalization strategies for the schema, explaining their trade-offs.
5.  **Implement a Trigger:** Design and implement a trigger and its associated function in PL/pgSQL that is relevant to the subject area.

## Functional Dependency (FD) Analysis

The first step was to identify the functional dependencies within the initial schema. A functional dependency X -> Y means that the value of attribute set X uniquely determines the value of attribute set Y.

-   **City:**
    -   `{name}` -> `{population, founding_date, description}`
    -   Candidate Keys: `{city_id}`, `{name}`
-   **Character:**
    -   `{citizen_id}` -> `{name, role, age, skills, status}`
    -   Candidate Keys: `{character_id}`, `{citizen_id}`
-   **Zone:**
    -   `{city_id, zone_name}` -> `{zone_type, security_level, description}`
    -   Candidate Keys: `{zone_id}`, `{city_id, zone_name}`
-   **SecuritySystem:**
    -   `{zone_id}` -> `{security_type, status, installation_date}`
    -   Candidate Keys: `{system_id}`, `{zone_id}`
-   **Door:**
    -   `{System_ID, door_number_within_system}` -> `{Door_Type, Access_Code, Status}`
    -   Candidate Keys: `{Door_ID}`, `{System_ID, door_number_within_system}`
-   **AccessRecord:**
    -   `{Character_ID, Door_ID, Access_Time}` -> `{Access_Status}`
    -   Candidate Keys: `{Access_ID}`, `{Character_ID, Door_ID, Access_Time}`
-   **Event:**
    -   `{Access_ID, Event_Type, Timestamp}` -> `{Details}`
    -   Candidate Keys: `{Event_ID}`, `{Access_ID, Event_Type, Timestamp}`
-   **EscapeAttempt:**
    -   `{Character_ID, Zone_ID, Attempt_Time}` -> `{Outcome}`
    -   Candidate Keys: `{Attempt_ID}`, `{Character_ID, Zone_ID, Attempt_Time}`
-   **Mission:**
    -   `{name}` -> `{objective, start_date, end_date, status}`
    -   Candidate Keys: `{mission_id}`, `{name}`
-   **MissionParticipation:**
    -   `{Character_ID, Mission_ID}` -> `{Assigned_Role, Contribution}`
    -   Candidate Keys: `{Participation_ID}`, `{Character_ID, Mission_ID}`
-   **VoiceInterface:**
    -   `{System_ID}` -> `{Voice_Type, Language, Response_Time}`
    -   Candidate Keys: `{Interface_ID}`, `{System_ID}`

## Normalization Analysis

The schema from Lab 1 was analyzed against the normal forms.

### First Normal Form (1NF)
The schema satisfies 1NF because:
1.  **Atomicity:** Every attribute in every table holds a single, atomic value.
2.  **No Repeating Groups:** There are no multi-valued columns.
3.  **Unique Rows:** Every table has a primary key that uniquely identifies each row.

### Second Normal Form (2NF)
The schema satisfies 2NF because it is in 1NF and contains no partial dependencies.
-   **For tables with simple primary keys** (e.g., `City`, `Character`), partial dependencies are impossible by definition.
-   **For tables with composite candidate keys** (e.g., `Zone` with CK `{city_id, name}`), all non-key attributes like `security_level` depend on the *entire* key, not just a part of it. The security level is specific to a particular zone within a particular city.

### Third Normal Form (3NF)
The schema satisfies 3NF because it is in 2NF and contains no transitive dependencies. In every table, all non-key attributes depend directly on the primary key and not on any other non-key attribute.
-   For example, in the `City` table, `description` depends directly on `city_id` (or `name`), not on `population`.

### Boyce-Codd Normal Form (BCNF)
A relation is in BCNF if, for every non-trivial functional dependency X -> Y, X is a superkey.
**Conclusion: The schema designed in Lab 1 is already in BCNF.**
-   **Proof:** For every functional dependency identified in all tables, the determinant (the left side of the arrow) is a candidate key, and therefore a superkey.
    -   In `City`, both determinants (`city_id` and `name`) are candidate keys.
    -   In `Character`, both `character_id` and `citizen_id` are candidate keys.
    -   This holds true for all other relations in the schema.

Since the original schema was already fully normalized to BCNF, no structural changes were required for this part of the assignment.

## Denormalization Strategies

Although the schema is well-normalized, certain denormalizations could improve query performance in specific scenarios.

### Strategy 1: Merging One-to-One Related Tables
-   **Description:** The `SecuritySystem` and `VoiceInterface` tables have a strict one-to-one relationship. They can be merged into a single table, `SecuritySystem_Voice_Combined`.
-   **Advantages:**
    -   Eliminates a `JOIN` operation when retrieving complete security system information, speeding up queries.
    -   Slightly simplifies the schema by reducing the number of tables.
-   **Disadvantages:**
    -   Creates a "wider" table, which can be inefficient if queries frequently request only security system data or only voice interface data, but not both.
    -   If the voice interface were optional (it is mandatory in our model), this would introduce `NULL` values into the combined table.

### Strategy 2: Adding a Redundant Attribute for Performance
-   **Description:** Add a `character_name` attribute to the `AccessRecord` table.
-   **Advantages:**
    -   When querying access logs, the character's name can be displayed directly from the `AccessRecord` table without needing to `JOIN` with the `Character` table. This can significantly improve performance, especially for large log tables.
-   **Disadvantages:**
    -   **Data Redundancy:** The character's name is now stored in two places.
    -   **Update Anomaly:** If a character's name changes in the `Character` table, it must also be updated in every corresponding entry in the `AccessRecord` table, which is complex and error-prone.

## PL/pgSQL Trigger Implementation

### Objective
To enhance the system's security logging, a trigger was designed to automatically create a 'Security Alert' event in the `Event` table whenever a denied access attempt is recorded in the `AccessRecord` table.

### PL/pgSQL Function: `log_denied_access_event()`
This function contains the logic to be executed by the trigger. It inserts a new record into the `Event` table.

```sql
CREATE OR REPLACE FUNCTION log_denied_access_event()
RETURNS TRIGGER AS $$
BEGIN
    -- This is a trigger function, callable only by a trigger.
    INSERT INTO Event (Access_ID, Event_Type, Timestamp, Details)
    VALUES (
        NEW.Access_ID,
        'SECURITY ALERT!!',
        now(),
        'Access denied attempt logged for door ' || NEW.Door_ID || ' by character ID: ' || NEW.Character_ID
    );
    
    -- For an AFTER trigger, the return value is ignored, but it's good practice.
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

### Trigger Definition: `trg_log_denied_access`
This trigger is attached to the `AccessRecord` table and calls the function under specific conditions.

```sql
CREATE TRIGGER trg_log_denied_access
AFTER INSERT ON AccessRecord
FOR EACH ROW
WHEN (NEW.Access_Status = 'Denied')
EXECUTE FUNCTION log_denied_access_event();
```
-   **`AFTER INSERT ON AccessRecord`**: The trigger fires after a new row is inserted.
-   **`FOR EACH ROW`**: The trigger logic is executed for each individual row that is inserted.
-   **`WHEN (NEW.Access_Status = 'Denied')`**: This crucial clause ensures the trigger only fires if the `Access_Status` of the new record is 'Denied', making it efficient.

## Conclusion

During this laboratory work, I became acquainted with the concepts of normalization and denormalization. I learned how to identify functional dependencies in a model and analyze it for compliance with various normal forms (1NF, 2NF, 3NF, BCNF). I also became familiar with the procedural language PL/pgSQL, using it to implement a trigger that enforces business logic automatically. Finally, I studied effective ways to denormalize a database schema and the specific situations in which their application might be beneficial.
