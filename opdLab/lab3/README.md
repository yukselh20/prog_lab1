# Computer Architecture Lab 3: Array Processing and Control Flow in Assembly

This repository contains the analysis and solution for the third laboratory assignment in the "Computer Architecture" course. The project involves reverse-engineering a more complex assembly language program that processes an array of data. The key tasks are to determine the program's algorithm, define the valid data ranges, understand the use of different addressing modes, and trace the program's control flow, which includes loops and conditional branching.

| **Course**            | Fundamentals of Professional Activities (OPD) |
| --------------------- | --------------------------------------------- |
| **Lab No.**           | 3                                             |
| **Variant**           | 3496                                          |
| **Student**           | Hamza Yüksel                                  |
| **Group**             | P3132                                         |
| **Instructor**        | Yaroslav Abuzov                               |

## Table of Contents
- [Assignment Description](#assignment-description)
- [Program Analysis](#program-analysis)
  - [Memory Layout](#memory-layout)
  - [Program Description and Algorithm](#program-description-and-algorithm)
  - [Instruction Breakdown and Control Flow](#instruction-breakdown-and-control-flow)
- [Data Representation and Constraints](#data-representation-and-constraints)
  - [Representation Domain](#representation-domain)
  - [Valid Domain](#valid-domain)
- [Execution Trace](#execution-trace)
- [Conclusion](#conclusion)

## Assignment Description

The objective of this assignment is to analyze a given assembly program (Variant 3496) by:
1.  **Reconstructing the program's source code** and determining its purpose.
2.  **Creating a detailed description** of the program's algorithm.
3.  **Defining the Representation Domain (ОП)** and **Valid Domain (ОДЗ)** for the input data and the result.
4.  **Performing a comprehensive execution trace** of the program to show the state of the registers at each step.

## Program Analysis

The provided program operates on an array of three 16-bit signed integers and identifies a specific value among them.

### Memory Layout

| Address | Variable/Constant | Description |
|---|---|---|
| `2CC` | `A` | A pointer to the *first* element of the array. |
| `2CD` | `B` | A pointer to the *last* element of the array. This is used for iterating backwards. |
| `2CE` | `C` | The loop counter, initialized to the number of elements in the array (3). |
| `2CF` | `R` | The memory location where the final result will be stored. |
| `2D0`-`2DF` | - | Program Instructions. |
| `2E0`-`2E2` | `M[0]`, `M[1]`, `M[2]`| The three elements of the data array. |

### Program Description and Algorithm

The program's primary function is to **find the smallest non-negative (i.e., positive or zero) value in a given array and store it at a specific memory location.**

The algorithm works as follows:
1.  **Initialization:**
    -   A very large positive number (`0x7FFF`, or `32767`) is loaded and stored in the result location `R` (`2CF`). This acts as an initial "maximum" value.
    -   A loop counter `C` (`2CE`) is initialized to `3`.
    -   A pointer `B` (`2CD`) is initialized to point to the last element of the array (`2E2`).
2.  **Iteration (Loop):** The program iterates through the array from the last element to the first. In each iteration:
    -   The pointer `B` is decremented (`LD (IP+1+F4)` - Indirect auto-decrement).
    -   The current array element pointed to by `B` is loaded into the accumulator.
    -   The sign of the element is checked (`BMI` - Branch on Minus). If the number is negative, it is ignored, and the loop continues to the next element.
    -   If the element is non-negative, it is compared with the current minimum value stored in `R` (`CMP (IP+1+F4)`).
    -   If the current element is *less than* the value in `R`, it becomes the new minimum. The program branches (`BGE` is skipped) and stores this new minimum value in `R` (`ST #F2`).
3.  **Termination:** The loop continues until the counter `C` (`LOOP` instruction) reaches zero. The program then halts (`HLT`).

### Instruction Breakdown and Control Flow

The program demonstrates several key assembly concepts:
-   **Immediate Addressing (`LD #...`):** Used to load constant values directly into the accumulator.
-   **Direct Relative Addressing (`ST #...`):** Used to store the accumulator's value at an address calculated relative to the Instruction Pointer (IP).
-   **Indirect Auto-Decrement Addressing (`LD (IP+1+F4)`):** A powerful mode where a memory location (the pointer) is first decremented, and then the value at that new address is loaded. This is key to iterating backward through the array.
-   **Conditional Branching (`BMI`, `BGE`):** Used to control the program flow based on the result of comparisons (sign check, magnitude comparison).
-   **Looping (`LOOP`):** A dedicated instruction that decrements a counter and jumps if the counter is not yet zero.

## Data Representation and Constraints

### Representation Domain
-   **Array Elements (`M[0]`, `M[1]`, `M[2]`):** 16-bit signed integers (`-32768` to `32767`).
-   **Pointers (`A`, `B`):** 11-bit unsigned addresses.
-   **Loop Counter (`C`):** 8-bit unsigned integer (`0` to `255`).
-   **Result (`R`):** 16-bit signed integer.

### Valid Domain
-   **`M[I]`:** Can be any value in the 16-bit signed integer range (`-2^15` to `2^15 - 1`).
-   **`C`:** The number of elements to process, `C ∈ [0, 2^8 - 1]`.
-   **`R`:** The result will always be a non-negative number, so `R ∈ [0, 2^15 - 1]`.
-   **Pointers (`A`, `B`):** Must point to valid memory locations within the address space (e.g., `0` to `0x7FF`).

## Execution Trace

The following table provides a step-by-step trace of the program's execution, showing the state of key registers and memory locations.

| Addr | IP  | CR   | AR  | DR   | AC   | NZVC | Modified Addr | New Value |
|------|-----|------|-----|------|------|------|---------------|-----------|
| 2D0  | 2D1 | AF80 | 2D0 | FF80 | FF80 | 1000 | -             | -         |
| 2D1  | 2D2 | 0740 | 2D1 | 0740 | FF7F | 1001 | -             | -         |
| 2D2  | 2D3 | 0680 | 2D2 | 0680 | 7FFF | 0001 | -             | -         |
| 2D3  | 2D4 | EEFB | 2CF | 7FFF | 7FFF | 0001 | `2CF`         | `7FFF`    |
| 2D4  | 2D5 | AF03 | 2D4 | 0003 | 0003 | 0001 | -             | -         |
| 2D5  | 2D6 | EEF8 | 2CE | 0003 | 0003 | 0001 | `2CE`         | `0003`    |
| **...Loop Start...** | | | | | | | | |
| 2D8  | 2D9 | ABF4 | 2E2 | 72DE | 72DE | 0000 | `2CD`         | `02E2` -> `02E1` |
| 2D9  | 2DA | F203 | 2D9 | F203 | 72DE | 0000 | -             | - (BMI fails) |
| 2DA  | 2DB | 7EF4 | 2CF | 7FFF | 72DE | 1000 | -             | - (CMP sets N flag) |
| 2DB  | 2DC | F901 | 2DB | F901 | 72DE | 1000 | -             | - (BGE fails) |
| 2DC  | 2DD | EEF2 | 2CF | 72DE | 72DE | 1000 | `2CF`         | `72DE`    |
| 2DD  | 2DE | 82CE | 2CE | 0002 | 72DE | 1000 | `2CE`         | `0002`    |
| **...Next Iteration...** | | | | | | | | |
| 2D8  | 2D9 | ABF4 | 2E1 | FD00 | FD00 | 1000 | `2CD`         | `02E1` -> `02E0` |
| 2D9  | 2DA | F203 | 2D9 | F203 | FD00 | 1000 | `2DE`         | - (BMI succeeds, jumps) |
| 2DE  | 2D8 | CEF9 | 2DE | ...  | ...  | ...  | -             | (Jumps back to loop start) |
| **...Final Iteration...** | | | | | | | | |
| **...Program halts at 2DF...** | | | | | | | | |

## Conclusion

During the execution of this laboratory work, I learned to work with arrays, branching, and loops in the context of the basic computer (БЭВМ) architecture. I studied direct and indirect addressing modes and the execution cycle of commands such as `LOOP` and `JUMP`. This assignment provided practical experience in analyzing assembly code with control flow, understanding how high-level iterative algorithms are implemented at a low level, and tracing the program's behavior through different execution paths.
