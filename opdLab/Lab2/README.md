# Computer Architecture Lab 2: Assembly Program Analysis and Optimization

This repository contains the analysis and solution for the second laboratory assignment in the "Computer Architecture" course. The project involves reverse-engineering a simple assembly language program for a basic computer architecture (БЭВМ). The core tasks include determining the function computed by the program, defining the valid ranges for its input and output data, tracing its execution step-by-step, and proposing an optimized version of the program that achieves the same result with fewer instructions.

| **Course**            | Computer Architecture (Исследование работы БЭВМ) |
| --------------------- | ------------------------------------------------ |
| **Lab No.**           | 2                                                |
| **Variant**           | 31099                                            |
| **Student**           | Hamza Yüksel                                     |
| **Group**             | P3132                                            |
| **Instructor**        | M.A. Nyagin                                      |

## Table of Contents
- [Assignment Description](#assignment-description)
- [Program Analysis](#program-analysis)
  - [Memory Layout](#memory-layout)
  - [Instruction Breakdown](#instruction-breakdown)
  - [Computed Function](#computed-function)
- [Data Representation and Constraints](#data-representation-and-constraints)
  - [Representation Domain (ОП)](#representation-domain-оп)
  - [Valid Domain (ОДЗ)](#valid-domain-одз)
- [Execution Trace](#execution-trace)
- [Program Optimization](#program-optimization)
  - [Analysis of Inefficiencies](#analysis-of-inefficiencies)
  - [Optimized Code](#optimized-code)
- [Conclusion](#conclusion)

## Assignment Description

The objective of this assignment is to analyze a given assembly program (Variant 31099). This involves:
1.  **Determining the function** computed by the program.
2.  **Defining the Representation Domain (ОП)** and the **Valid Domain (ОДЗ)** for the input and output data. All arithmetic operands are to be treated as signed numbers, and logical operands as sets of 16 bits.
3.  **Performing a detailed execution trace** of the program.
4.  **Proposing an optimized version** of the program that uses fewer instructions.

## Program Analysis

The provided program operates on three input variables (A, B, F) and produces one output (R).

### Memory Layout

| Address | Mnemonic | Initial Value/Variable | Role |
|---|---|---|---|
| `070` | `ПЕРЕМЕННАЯ A` | `A` (e.g., `F909`) | Input Variable A |
| `071`-`078` | - | - | Program Instructions |
| `079` | `ПЕРЕМЕННАЯ B` | `B` (e.g., `FFFF`) | Input Variable B |
| `07A` | `РЕЗУЛЬТАТ R` | `R` (initially empty) | Final Result |
| `07B` | `ПЕРЕМЕННАЯ D` | `D` (initially empty) | Intermediate Storage |
| `07C` | `ПЕРЕМЕННАЯ F` | `F` (e.g., `A070`) | Input Variable F |

### Instruction Breakdown

| Address | Mnemonic | Description | Accumulator (AC) State Change |
|---|---|---|---|
| `071` | `LD 070` | Load the value of variable A from address `070` into the Accumulator. | `AC = A` |
| `072` | `AND 079` | Perform a bitwise AND between the AC and the value of B from `079`. | `AC = AC & B` (i.e., `A & B`) |
| `073` | `ST 07B` | Store the result of the AND operation (`A & B`) into memory address `07B` (variable D). | `(07B) = AC` |
| `074` | `CLA` | Clear the Accumulator. | `AC = 0` |
| `075` | `ADD 07C` | Add the value of variable F from `07C` to the cleared AC. | `AC = 0 + F` (i.e., `F`) |
| `076` | `SUB 07B` | Subtract the intermediate value D (which is `A & B`) from the AC. | `AC = AC - (07B)` (i.e., `F - (A & B)`) |
| `077` | `ST 07A` | Store the final result from the AC into memory address `07A` (variable R). | `(07A) = AC` |
| `078` | `HLT` | Halt the program. | - |

### Computed Function

By analyzing the sequence of operations, the program is found to compute the following mathematical function:

**`R = F - (A & B)`**

The program first calculates the bitwise AND of variables A and B, stores this intermediate result, and then subtracts it from variable F. The final result is stored in R.

## Data Representation and Constraints

The architecture uses 16-bit words.

### Representation Domain (ОП)
This defines the range of values that can be represented by the 16-bit architecture.
-   **For logical operations:** Unsigned integers from `0` to `65535` (`2^16 - 1`).
-   **For arithmetic operations:** Signed integers (two's complement) from `-32768` (`-2^15`) to `32767` (`2^15 - 1`).

### Valid Domain (ОДЗ)
This defines the constraints on the input and output values to prevent overflow and ensure the correctness of the signed arithmetic result `R`. The result of the logical `A & B` operation is treated as a signed integer for the subtraction.
-   **Input Variables:** `A` and `B` are treated as 16-bit logical values. `F` is a 16-bit signed integer.
-   **Output Variable:** `R` is a 16-bit signed integer.

The primary constraint is that the final result `R` must fit within the signed 16-bit range:
**`-32768 <= F - (A & B) <= 32767`**

## Execution Trace

This trace demonstrates the step-by-step execution of the program, showing the state of CPU registers after each instruction.

| Addr | IP | CR | AR | DR | SP | BR | AC | PS (NZVC) | Modified Memory |
|---|---|---|---|---|---|---|---|---|---|
| `071` | 072 | A070 | 071 | A070 | 000 | 0071 | `2079` | 0000 | - |
| `072` | 073 | 2079 | 079 | 2070 | 000 | 0072 | `2070` | 0000 | - |
| `073` | 074 | E07B | 07B | 2070 | 000 | 0073 | 2070 | 0000 | `(07B) = 2070` |
| `074` | 075 | 0200 | 074 | 0200 | 000 | 0074 | `0000` | 0100 | - |
| `075` | 076 | 407C | 07C | A070 | 000 | 0075 | `A070` | 0100 | - |
| `076` | 077 | 607B | 07B | 2070 | 000 | 0076 | `8000` | 1001 | - |
| `077` | 078 | E07A | 07A | 8000 | 000 | 0077 | 8000 | 1001 | `(07A) = 8000` |
| `078` | 079 | 0100 | 078 | 0100 | 000 | 0078 | 8000 | 1001 | - (Halt) |

## Program Optimization

The original program can be made more efficient by eliminating redundant instructions and memory operations.

### Analysis of Inefficiencies
1.  **Redundant `CLA`:** The `CLA` instruction at address `074` is unnecessary. It clears the accumulator, which is then immediately overwritten by the `ADD 07C` instruction. A `LD` instruction would achieve the same result in one step.
2.  **Unnecessary Intermediate Storage:** The program uses memory address `07B` to store the intermediate result of `A & B`. This requires an extra `ST` (store) and `SUB` (subtract from memory) instruction. A more efficient approach would be to keep this intermediate result in a register if the architecture supported it, or to restructure the calculation to avoid the store-load cycle.

### Optimized Code

The optimized program calculates the same function `R = F - (A & B)` but uses two fewer instructions and one less memory location for intermediate data.

| Address | Mnemonic | Description |
|---|---|---|
| `071` | `LD 070` | Load A into AC. `AC = A` |
| `072` | `AND 078` | Perform bitwise AND with B. `AC = A & B` |
| `073` | `ST 079` | Store the result (`A & B`) directly into the result memory location R (address `079`). `(079) = A & B` |
| `074` | `LD 07A` | Load F into AC. `AC = F` |
| `075` | `SUB 079` | Subtract the stored result (`A & B`) from F. `AC = F - (A & B)` |
| `076` | `ST 079` | Store the final result back into R. `(079) = AC` |
| `077` | `HLT` | Halt the program. |

**Note on Memory Layout for Optimized Version:**
-   `078`: Variable B
-   `079`: Result R
-   `07A`: Variable F

This version avoids the `CLA` instruction and eliminates the need for the separate intermediate memory cell `D` at address `07B`, thus saving two memory locations.

## Conclusion

During this laboratory work, I became acquainted with the structure of a basic computer (БЭВМ), learned how its main components are arranged and connected, and learned to determine the valid domain (ОДЗ) for program data. I analyzed the structure and types of commands, understood how data is represented in the computer's memory, and wrote my own program equivalent to the given task, thereby saving two memory cells.
