---

# Ticket Collection Management Console Application

## Overview

This is a console-based Java application that allows you to manage a collection of Ticket objects interactively. The application loads Ticket data from a JSON file at startup, supports a variety of commands to manipulate the collection, and saves changes back to the file. It demonstrates key Java features such as Collections, generics, file I/O, and robust error handling—all implemented following SOLID principles.

## Features

- **Collection Management:**  
  - Tickets are stored in a `LinkedHashMap<Long, Ticket>` to preserve insertion order and allow fast, key-based access.
  - The collection is automatically populated from a JSON file (the file path is specified via the environment variable `TICKET_FILE`).

- **Interactive Command Interface:**  
  The application supports a range of commands, including:
  - `help`: Displays a list of available commands.
  - `info`: Shows details about the collection (type, initialization time, number of elements, etc.).
  - `show`: Lists all Ticket objects.
  - `insert <key> {element}`: Inserts a new Ticket with a specified key.
  - `update <id> {element}`: Updates an existing Ticket (found by its unique id).
  - `remove_key <key>`: Removes a Ticket based on its key.
  - `clear`: Clears the entire collection.
  - `save`: Saves the current collection back to the JSON file.
  - `execute_script <file_name>`: Reads and executes commands from a script file (commands in the same format as interactive input).
  - `exit`: Exits the application without saving.

- **Input Validation and Robust Error Handling:**  
  - User inputs are rigorously validated using form classes and static factory methods. For instance, only valid Ticket types (e.g., VIP, USUAL, BUDGETARY, CHEAP) are accepted.
  - Custom exceptions provide clear error messages when inputs do not meet requirements.
  
- **JSON Serialization:**  
  - The application uses Gson for JSON serialization/deserialization.
  - Custom TypeAdapters (e.g., for `ZonedDateTime`) ensure proper handling of Java date/time types.

## Requirements

- **Java 11 or higher** (tested with JDK 21)
- **Gson Library** (e.g., version 2.12.1)
- The file for storage must be specified via the `TICKET_FILE` environment variable.

## Getting Started

### Environment Setup

1. **Clone the Repository:**  
   Clone this repository to your local machine.

2. **Set the Environment Variable:**  
   Set the `TICKET_FILE` environment variable to the path of your JSON file. For example:
   - On Linux/Mac:
     ```bash
     export TICKET_FILE=path/to/tickets.json
     ```
   - On Windows:
     ```cmd
     set TICKET_FILE=path\to\tickets.json
     ```

3. **Dependencies:**  
   Ensure that the Gson library is available on your classpath.

### Building and Running

- **Using an IDE:**  
  Import the project into your IDE (e.g., IntelliJ IDEA or Eclipse). Configure the run/debug configuration to include the environment variable, then run the `Main` class.

- **Command Line:**
  1. Compile the project:
     ```bash
     javac -cp gson-2.12.1.jar -d out src/**/*.java
     ```
  2. Run the project:
     ```bash
     java -cp out:gson-2.12.1.jar Main
     ```
  (Adjust the classpath separator `:` or `;` according to your operating system.)

## Detailed Design and Implementation

### Collections, Sorting, Comparable, and Comparator

- **Comparable Interface:**  
  The `Ticket` class implements the `Comparable` interface by overriding `compareTo`, which defines a natural order (e.g., by id). This allows sorting Ticket objects without requiring external comparators.
  
- **Comparator and Streams:**  
  In commands like `print_descending`, Java Streams and `Comparator` are used to sort the collection. For example:
  ```java
  List<Ticket> sorted = collectionManager.getCollection().values().stream()
      .sorted(Comparator.comparing(Ticket::getPrice).reversed())
      .collect(Collectors.toList());
  ```
  This sorts tickets by their price in descending order.

### Categories of Collections and Map Implementation

- **Lists, Sets, and Maps:**  
  While Java offers several collection types, our application uses a `LinkedHashMap` to store Ticket objects. This ensures that the insertion order is maintained and allows fast key-based access.
  
- **Usage in the Project:**  
  Tickets are stored in the map with keys provided by the user (via the `insert` command), while each Ticket also has an internally managed unique id.

### Parameterized Types and Generics

- The project makes extensive use of parameterized types. For instance, `LinkedHashMap<Long, Ticket>` ensures type safety.
- We use `TypeToken` from Gson to properly deserialize generic types from JSON.

### Wrapper Classes and Autoboxing

- Wrapper classes (e.g., `Long` for keys) allow us to use primitive values in collections that require objects.
- Autoboxing and unboxing automatically convert between primitives and their corresponding wrapper classes, simplifying code and ensuring type safety.

### I/O Flows and File Handling

- **Byte vs. Character Streams:**  
  The application uses character streams (`InputStreamReader` and `FileWriter`) to read and write text (JSON) data.
  
- **Flow Chains:**  
  Buffered streams or readers/writers can be used to enhance performance, though our basic implementation directly uses the necessary classes.
  
- **File Operations:**  
  The `DumpManager` class handles file I/O:
  - Reading data using `InputStreamReader`
  - Writing data using `FileWriter`
  
- **Working with Files:**  
  We use `java.io.File` to check file existence and manage file paths.

### Package java.nio

- Although our application currently uses the classic I/O API, the `java.nio` package provides non-blocking, high-performance I/O operations. It includes buffers, channels, and selectors. This package can be considered for future performance enhancements.

### Javadoc and Automatic Code Documentation

- All classes and methods are documented using Javadoc comments. This not only improves code readability but also allows the generation of comprehensive API documentation.
- For example:
  ```java
  /**
   * Represents a Ticket in the system.
   * 
   * @param name the name of the ticket; must not be null or empty.
   * @param coordinates the ticket's location; must be valid.
   * @param price the price of the ticket; must be greater than 0.
   * @param discount the discount percentage; must be between 1 and 100.
   */
  public Ticket(String name, Coordinates coordinates, int price, long discount, String comment, TicketType type, Event event) { ... }
  ```

## Command Reference

The following commands are supported:

- **help:** Displays help for all available commands.
- **info:** Shows collection metadata (type, initialization time, element count).
- **show:** Prints all Ticket objects in the collection.
- **insert `<key>` {element}:** Adds a new Ticket to the collection under the given key.
- **update `<id>` {element}:** Updates an existing Ticket identified by its id.
- **remove_key `<key>`:** Removes the Ticket associated with the specified key.
- **clear:** Clears the entire collection.
- **save:** Saves the current collection to the JSON file.
- **execute_script `<file_name>`:** Reads and executes commands from a script file.
- **exit:** Exits the program without saving.
- Additional commands include: `remove_lower`, `history`, `replace_if_lower`, `count_greater_than_discount`, `filter_starts_with_name`, and `print_descending`.

## Conclusion

This Ticket Collection Management Console Application is designed with modularity and maintainability in mind. It leverages Java's powerful collection framework, generics, and I/O APIs to provide a robust and interactive solution for managing Ticket objects. Contributions and improvements are welcome!

---
