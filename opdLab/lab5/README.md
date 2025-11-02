# OPD Lab 5: Asynchronous I/O and Device Communication

This repository contains the solution and analysis for the fifth laboratory assignment of the "Fundamentals of Professional Activities" course. The project involves developing an assembly language program to perform asynchronous data exchange with an external device. The program's primary function is to output a string of characters to an output device (VU-3), using a "ready" signal (polling) to synchronize the data transfer.

| **Course**            | Fundamentals of Professional Activities (OPD) |
| --------------------- | --------------------------------------------- |
| **Lab No.**           | 5                                             |
| **Variant**           | 119                                           |
| **Student**           | Hamza Yüksel                                  |
| **Group**             | P3132                                         |
| **Instructor**        | V. V. Nikolaev                                |

## Table of Contents
- [Assignment Description](#assignment-description)
- [Program Analysis](#program-analysis)
  - [Purpose and Algorithm](#purpose-and-algorithm)
  - [Memory Layout](#memory-layout)
  - [Asynchronous I/O (Polling)](#asynchronous-io-polling)
  - [Data Format and Processing](#data-format-and-processing)
- [Data Representation and Constraints](#data-representation-and-constraints)
  - [Representation Domain](#representation-domain)
  - [Valid Domain](#valid-domain)
- [Assembler Source Code](#assembler-source-code)
- [Conclusion](#conclusion)

## Assignment Description

The objective of this assignment is to develop an assembly program for asynchronous data exchange with an external device (Variant 119).

**Key Requirements:**
1.  **Functionality:** The program must perform asynchronous data **output** to Output Device 3 (VU-3).
2.  **Program Location:** The program code starts at memory address `0x174`.
3.  **Data Location:** The string to be output is located at memory address `0x582`.
4.  **Character Encoding:** The string is represented in **ISO-8859-5** encoding.
5.  **Memory Format:** The string is stored in memory with a specific little-endian format: `ADDR1: CHAR2 CHAR1`, `ADDR2: CHAR4 CHAR3`, etc. Each 16-bit word holds two characters, with the second character in the lower byte and the first in the upper byte.
6.  **Termination:** The output process must terminate upon encountering a stop symbol with the code `0x0A` (Newline).

## Program Analysis

### Purpose and Algorithm

The program's purpose is to read a null-terminated (specifically, newline-terminated) string from a predefined memory location and send it, character by character, to an external output device. The data transfer is asynchronous, meaning the program must wait for the device to signal that it is ready to receive the next character before sending it.

The algorithm is as follows:
1.  Initialize a pointer (`ADR`) to the start of the string array.
2.  **Enter the main loop (`SYM1`):**
    a. **Poll for readiness:** Continuously check the status register of the output device until the "ready" bit is set.
    b. **Load data:** Load the 16-bit word from the memory address pointed to by `ADR`.
    c. **Process first character:** Isolate the lower 8 bits (the first character, e.g., `CHAR1`).
    d. **Send first character:** Send the isolated character to the device's data register.
    e. **Check for termination:** Compare the sent character with the stop symbol (`0x0A`). If they match, jump to the `STOP` label and halt.
    f. **Poll for readiness again:** Wait for the device to be ready for the second character.
    g. **Process second character:** Load the same 16-bit word again, but this time **post-increment the pointer `ADR`** to prepare for the next loop iteration (`LD (ADR)+`). Swap the upper and lower bytes to access the second character (e.g., `CHAR2`), then isolate it.
    h. **Send second character:** Send the second character to the device.
    i. **Check for termination:** Compare the second character with the stop symbol. If they match, halt.
    j. **Loop:** If the stop symbol was not found, jump back to the beginning of the main loop (`SYM1`) to process the next word from the incremented address.

### Memory Layout

| Address | Label        | Content/Description                                |
|---------|--------------|----------------------------------------------------|
| `0x174` | `ADR`        | A 16-bit pointer to the start of the string (`$ARRAY`). |
| `0x175` | `STOP_SYMBL` | The termination character (`0x0A`).                |
| `0x176`-`0x188`| -            | Program instructions.                              |
| `0x582` | `ARRAY`      | Start of the string data (`0xF0C9`, `0xD7DE`, ...). |

### Asynchronous I/O (Polling)

The program synchronizes with the slow external device using a polling loop. This is a form of busy-waiting.
```assembly
SYM1: IN 7         ; Read status from device port 7
      AND #0x40    ; Isolate the readiness bit (bit 6)
      BEQ SYM1     ; If the result is zero (bit 6 is not set), loop and wait
```
This loop executes repeatedly until the device sets bit 6 of its status register, indicating it is ready to accept a character. The program contains two such loops, one for each character in a 16-bit word.

### Data Format and Processing

The core challenge of this program is handling the unique `CHAR2 CHAR1` memory format.
-   **To get the first character:** The program loads the 16-bit word and masks the lower 8 bits.
    ```assembly
    LD (ADR)       ; Load word, e.g., 0xF0C9, into Accumulator (AC)
    AND #0xFF      ; AC becomes 0x00C9. The character 'C9' is isolated
    OUT 6          ; Send 'C9' to the output device
    ```
-   **To get the second character:** The program re-loads the word (and increments the pointer for the next iteration), then swaps the bytes.
    ```assembly
    LD (ADR)+      ; Load 0xF0C9 into AC, then set ADR to point to the next word
    SWAB           ; Swap bytes. AC becomes 0xC9F0
    AND #0xFF      ; AC becomes 0x00F0. The character 'F0' is isolated
    OUT 6          ; Send 'F0' to the output device
    ```

## Data Representation and Constraints

### Representation Domain
-   **Characters:** 8-bit values, interpreted according to the ISO-8859-5 standard.
-   **Memory Words:** 16-bit values.
-   **Addresses/Pointers:** 16-bit memory addresses.
-   **Device Status:** A bitmask where bit 6 (`0x40`) signifies readiness.

### Valid Domain
-   The string data stored at `ARRAY` must be terminated by a character with the code `0x0A`.
-   The `ADR` pointer must initially point to a valid memory location containing the string data.
-   The character codes should be valid within the ISO-8859-5 character set.

## Assembler Source Code

```assembly
; Program Start Address
ORG 0x174

ADR:        WORD $ARRAY       ; Pointer to the string data
STOP_SYMBL: WORD 0x0A         ; Newline character for termination

; Main loop for processing one word (two characters)
SYM1:       IN 7              ; Poll VU: Read status register (port 7)
            AND #0x40         ; Check readiness bit (bit 6)
            BEQ SYM1          ; If not ready, loop
            
            LD (ADR)          ; Load the 16-bit word (e.g., CHAR2 CHAR1)
            AND #0xFF         ; Isolate the first character (CHAR1)
            OUT 6             ; Send the first character to VU (port 6)
            
            CMP STOP_SYMBL    ; Is it the stop character?
            BEQ STOP          ; If yes, finish the program

SYM2:       IN 7              ; Poll VU for the second character
            AND #0x40         ; Check readiness bit
            BEQ SYM2          ; If not ready, loop
            
            LD (ADR)+         ; Load the word again and post-increment the address pointer
            SWAB              ; Swap bytes to access the second character
            AND #0xFF         ; Isolate the second character (CHAR2)
            OUT 6             ; Send the second character to VU
            
            CMP STOP_SYMBL    ; Is it the stop character?
            BEQ STOP          ; If yes, finish
            
            JUMP SYM1         ; Go back to process the next word

STOP:       HLT               ; Halt the program

; Data Segment
ORG 0x582
ARRAY:      WORD 0xF0C9, 0xD7DE, 0x0A01 ; Example string data
```

## Conclusion

During the execution of this laboratory work, I studied the input/output devices in the basic computer architecture (БЭВМ) and their operation based on readiness signals. This involved understanding and implementing an asynchronous data transfer protocol using polling. I also practiced constructing assembler code, paying close attention to addressing modes (indirect and post-increment), bitwise operations for masking, and control flow for looping and termination. This assignment provided practical insight into how software interacts with hardware at a low level.
