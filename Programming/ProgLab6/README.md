# Ticket Management System (Lab Work 6)

This project is a client-server application for managing a collection of `Ticket` objects. It is a distributed system refactored from a monolithic console application (Lab 5), adhering to the principles of modular design, non-blocking network I/O, and object-based communication.

## Assignment (Variant)

<details>
<summary>Click to view original assignment text (Russian)</summary>

```
Внимание! У разных вариантов разный текст задания!
Разделить программу из лабораторной работы №5 на клиентский и серверный модули. Серверный модуль должен осуществлять выполнение команд по управлению коллекцией. Клиентский модуль должен в интерактивном режиме считывать команды, передавать их для выполнения на сервер и выводить результаты выполнения.

Необходимо выполнить следующие требования:

Операции обработки объектов коллекции должны быть реализованы с помощью Stream API с использованием лямбда-выражений.
Объекты между клиентом и сервером должны передаваться в сериализованном виде.
Объекты в коллекции, передаваемой клиенту, должны быть отсортированы по названию
Клиент должен корректно обрабатывать временную недоступность сервера.
Обмен данными между клиентом и сервером должен осуществляться по протоколу UDP
Для обмена данными на сервере необходимо использовать датаграммы
Для обмена данными на клиенте необходимо использовать сетевой канал
Сетевые каналы должны использоваться в неблокирующем режиме.
Обязанности серверного приложения:

Работа с файлом, хранящим коллекцию.
Управление коллекцией объектов.
Назначение автоматически генерируемых полей объектов в коллекции.
Ожидание подключений и запросов от клиента.
Обработка полученных запросов (команд).
Сохранение коллекции в файл при завершении работы приложения.
Сохранение коллекции в файл при исполнении специальной команды, доступной только серверу (клиент такую команду отправить не может).
Серверное приложение должно состоять из следующих модулей (реализованных в виде одного или нескольких классов):
Модуль приёма подключений.
Модуль чтения запроса.
Модуль обработки полученных команд.
Модуль отправки ответов клиенту.
Сервер должен работать в однопоточном режиме.
Обязанности клиентского приложения:

Чтение команд из консоли.
Валидация вводимых данных.
Сериализация введённой команды и её аргументов.
Отправка полученной команды и её аргументов на сервер.
Обработка ответа от сервера (вывод результата исполнения команды в консоль).
Команду save из клиентского приложения необходимо убрать.
Команда exit завершает работу клиентского приложения.
Важно! Команды и их аргументы должны представлять из себя объекты классов. Недопустим обмен "простыми" строками. Так, для команды add или её аналога необходимо сформировать объект, содержащий тип команды и объект, который должен храниться в вашей коллекции.
Дополнительное задание:
Реализовать логирование различных этапов работы сервера (начало работы, получение нового подключения, получение нового запроса, отправка ответа и т.п.) с помощью Logback
```

</details>

## Features

The client supports the following commands to manage the ticket collection on the server:

| Command                             | Description                                                                          |
| ----------------------------------- | ------------------------------------------------------------------------------------ |
| `help`                              | Displays help for available commands.                                                |
| `info`                              | Displays information about the collection (type, size, initialization date).       |
| `show`                              | Displays all elements in the collection, sorted by name.                             |
| `insert <key>`                      | Adds a new element with the specified key after prompting for element data.          |
| `update <id>`                       | Updates the element with the given ID after prompting for new data.                  |
| `remove_key <key>`                  | Removes an element from the collection by its key.                                   |
| `clear`                             | Clears the entire collection.                                                        |
| `execute_script <file_name>`        | Executes commands from a script file located on the server.                          |
| `remove_lower`                      | Removes all elements smaller than a new, user-specified element.                     |
| `replace_if_lower <key>`            | Replaces the element with the given key if a new element is smaller.                 |
| `count_greater_than_discount <val>` | Counts elements whose discount is greater than the specified value.                  |
| `filter_starts_with_name <prefix>`  | Filters and displays elements whose name starts with the given prefix.               |
| `print_descending`                  | Displays all elements in descending order of their price.                            |
| `exit`                              | Exits the client application.                                                        |

## Technologies Used

-   **Java 17+**: Core programming language.
-   **Java NIO (New I/O)**: For high-performance, non-blocking network communication.
    -   `DatagramChannel`: Used for UDP communication.
    -   `Selector`: Used by the server to handle multiple client requests on a single thread.
    -   `ByteBuffer`: Used as a buffer for network data.
-   **UDP Protocol**: The underlying network protocol for communication.
-   **Java Serialization**: For converting Java objects (`Request`/`Response` DTOs) into a byte stream for network transfer.
-   **Java Stream API**: Used on the server for functional-style processing of the collection data (sorting, filtering, counting).
-   **Gson**: Used on the server-side for serializing the collection to a JSON file for persistence.
-   **SLF4J + Logback (Bonus)**: For comprehensive logging of server-side events.

## Architecture

The application is structured into three main modules (packages) to ensure a clear separation of concerns:

1.  **`common`**: A shared library module that contains code used by both the client and server.
    -   `dto`: Data Transfer Objects (`Request`, `Response`, `CommandType`) that define the communication protocol. These are pure data containers.
    -   `model`: The data models (`Ticket`, `Coordinates`, `Event`, etc.), all implementing `Serializable`.
    -   `util`: Common utilities like `SerializationUtils`.
    -   **Crucially, this module has no dependencies on the `client` or `server` modules.**

2.  **`server`**: The server-side application.
    -   `managers`: Contains `CollectionManager` (manages the in-memory collection) and `DumpManager` (manages file persistence).
    -   `processing`: Contains `CommandProcessor`, which holds the business logic for executing commands received in `Request` DTOs.
    -   `network`: Contains `UDPServer`, which handles all non-blocking network I/O using `DatagramChannel` and `Selector`.
    -   `ServerMain`: The entry point for the server application.

3.  **`client`**: The client-side console application.
    -   `forms`: Contains classes (`TicketForm`, etc.) for gathering multi-step user input to create `Ticket` objects.
    -   `network`: Contains `UDPClient`, which handles sending `Request` DTOs and receiving `Response` DTOs, including timeout and retry logic.
    -   `utility`: Contains `Runner` (the main interactive loop), `Interrogator` (manages input source), and `console` helpers.
    -   `ClientMain`: The entry point for the client application.

### Communication Flow

1.  **Client:** The `Runner` parses user input and creates a `Request` DTO.
2.  **Client:** The `UDPClient` serializes the `Request` DTO into bytes.
3.  **Network:** The `DatagramChannel` sends the bytes over UDP to the server.
4.  **Server:** The `UDPServer`'s `Selector` detects incoming data. The server reads the bytes and deserializes them back into a `Request` DTO.
5.  **Server:** The `CommandProcessor` executes the command based on the `Request`'s `CommandType` and data, interacting with `CollectionManager`.
6.  **Server:** A `Response` DTO is created with the result of the operation.
7.  **Server:** The `UDPServer` serializes the `Response` DTO into bytes.
8.  **Network:** The `DatagramChannel` sends the bytes back to the client's address.
9.  **Client:** The `UDPClient` receives the bytes and deserializes them into a `Response` DTO.
10. **Client:** The `Runner` receives the `Response` DTO and displays the result to the user via the `Console`.

## Setup and Installation

1.  **Prerequisites**:
    -   Java JDK 17 or higher.
    -   Apache Maven (for building the project).
2.  **Build**:
    -   Clone the repository.
    -   Open a terminal in the project's root directory.
    -   Run the following Maven command to compile and package the application:
        ```bash
        mvn clean package
        ```
    -   This will create JAR files in the `target` directory of each module (`common`, `server`, `client`).

## How to Run

### Step 1: Set the Environment Variable

The server needs to know where to save and load the collection file. Set the `TICKET_FILE` environment variable to the full path of your desired JSON file.

**On Linux/macOS:**
```bash
export TICKET_FILE="/path/to/your/data/tickets.json"
```

**On Windows (Command Prompt):**```cmd
set TICKET_FILE="C:\path\to\your\data\tickets.json"
```

### Step 2: Run the Server

Open a terminal and run the server using its JAR file.

```bash
# Example assuming a 'fat' JAR was created by the build process
java -jar server/target/server-jar-with-dependencies.jar [port]
```
-   `[port]` is optional. If not provided, it will use the default port (e.g., 54321).
-   You should see a message indicating the server has started.

### Step 3: Run the Client

Open a **new, separate terminal** and run the client.

```bash
# Example assuming a 'fat' JAR was created
java -jar client/target/client-jar-with-dependencies.jar [host] [port]
```
-   `[host]` is optional (defaults to `localhost`).
-   `[port]` is optional (defaults to the server's port).
-   You should see a `$` prompt, ready to accept commands.

## How to Use

Once the client is running, you can enter commands at the `$` prompt.

**Example 1: Simple Commands**
```
$ info
[SUCCESS]: Type: java.util.LinkedHashMap
Initialization date: ...

$ show
[SUCCESS]: Collection is empty.
-- Data: Empty List --
```

**Example 2: Adding an Element**
The `insert` command (and others requiring element data) will prompt you for input line by line.
```
$ insert 101
=> Enter Ticket data for key 101:
Enter Ticket name:
My First Ticket
Enter coordinate X (float > -661):
10.0
... (and so on) ...
[SUCCESS]: Ticket successfully added with key 101.
```

## Author

-   **Hamza Yüksel**
