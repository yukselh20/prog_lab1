# OPD Lab 4: Subroutines, Stack Operations, and Recursive-like Function Calls

This repository documents the analysis for the fourth laboratory assignment of the "Fundamentals of Professional Activities" course. This project involves the detailed reverse-engineering of a complex assembly language program that utilizes subroutines, stack manipulation (`PUSH`, `POP`), and multiple function calls to compute a piecewise mathematical function. The core tasks are to determine the program's overall purpose, define the valid domains for its inputs and outputs, and perform a complete execution trace.

| **Course**            | Fundamentals of Professional Activities (OPD) |
| --------------------- | --------------------------------------------- |
| **Lab No.**           | 4                                             |
| **Variant**           | 772                                           |
| **Student**           | Hamza Yüksel                                  |
| **Group**             | P3132                                         |
| **Instructor**        | Yaroslav Abuzov                               |

## Table of Contents
- [Assignment Description](#assignment-description)
- [Program Analysis](#program-analysis)
  - [Memory Layout](#memory-layout)
  - [Main Program Logic](#main-program-logic)
  - [Subroutine `F(t)` Logic](#subroutine-ft-logic)
  - [Computed Function](#computed-function)
- [Data Representation and Constraints](#data-representation-and-constraints)
  - [Representation Domain](#representation-domain)
  - [Valid Domain (ODZ)](#valid-domain-odz)
- [Execution Trace](#execution-trace)
- [Conclusion](#conclusion)

## Assignment Description

The objective of this assignment is to analyze a given assembly program (Variant 772) by:
1.  **Reconstructing the program's functionality** from its machine code.
2.  **Determining the program's purpose** and providing a detailed description of its algorithm, including the role of the subroutine.
3.  **Defining the Representation Domain (ОП)** and **Valid Domain (ОДЗ)** for the program's inputs, outputs, and intermediate values.
4.  **Performing a full execution trace** to demonstrate the program's step-by-step operation.

## Program Analysis

The application consists of a main program that orchestrates three separate calls to a single subroutine, accumulating the results to produce a final value.

### Memory Layout

| Address Range | Component | Description |
|---|---|---|
| `03B` - `054` | Main Program Code | The primary execution block that calls the subroutine. |
| `055` - `057` | Input Data | Memory locations for variables `Z`, `Y`, and `X`. |
| `058` | Result Data | Memory location for the final result `R`. |
| `65F` - `66C` | Subroutine Code | The function `F(t)` that is called multiple times. |
| `66D` - `66E` | Subroutine Constants| Memory locations for constants `Q` and `W`, used by `F(t)`. |

### Main Program Logic

The main program computes a final result `R` by invoking a subroutine `F` with three different arguments derived from the inputs `X`, `Y`, and `Z`. The logic is as follows:

1.  **Initialize `R = 0`**.
2.  **First Call:** Calculate `t1 = X + 1`, then call `F(t1)`. The result is retrieved, and `R` is updated: `R = F(X + 1)`.
3.  The program in the report seems to have an error. After the first call, it adds 1 to the result, `R = F(X+1) + 1`. This might be a mistake in the code, but we will analyze it as it is.
4.  **Second Call:** Calculate `t2 = Z - 1`, then call `F(t2)`. The result is added to `R`: `R = R + F(Z - 1)`.
5.  **Third Call:** Calculate `t3 = Y - 1`, then call `F(t3)`. The result is added to `R`: `R = R + F(Y - 1)`.
6.  The program in the report seems to have another error. It subtracts 1 from the result of the third call before adding it to R, so `R = R + (F(Y-1) -1)`.

The **`CALL`** instruction pushes the return address (the address of the next instruction) onto the stack, and the **`RET`** instruction at the end of the subroutine pops this address to resume execution. Arguments are passed to the subroutine via the stack (`PUSH`).

### Subroutine `F(t)` Logic

The subroutine, located at address `65F`, implements a piecewise function based on its input argument `t`. It uses two predefined constants:
-   `Q = 0xFA29 = -1495` (signed decimal)
-   `W = 0x005D = 93` (decimal)

The function is defined as:

```
f(t) = 
  3*t + W,  if t > 0
  Q,        if Q <= t <= 0
  3*t + W,  if t < Q
```

The assembly logic for this is:
1.  Load the argument `t` from the stack: `LD (SP+1)`.
2.  Check if `t` is zero (`BEQ`). If so, jump to the case that returns `Q`.
3.  Check if `t` is positive (`BPL`). If so, jump to the `3*t + W` calculation.
4.  If `t` is negative, check if it's less than `Q` (`BMI`). If it is, jump to `3*t + W`. Otherwise, return `Q`.
5.  The `3*t + W` calculation is implemented using `ASL` (Arithmetic Shift Left, equivalent to `*2`) and additions: `t*2 + t + W`.

### Computed Function

Combining the main program's logic with the subroutine's definition, the final formula computed is:

**`R = (F(X + 1) + 1) + F(Z - 1) + (F(Y - 1) - 1)`**

where `F(t)` is the piecewise function described above.

## Data Representation and Constraints

### Representation Domain
All variables (`X`, `Y`, `Z`, `Q`, `W`, `R`, and the subroutine argument `t`) are **16-bit signed integers** in two's complement format. The representable range is **`[-32768, 32767]`**.

### Valid Domain (ODZ)

For the program to execute without overflow, two conditions must be met:
1.  The inputs to the subroutine (`t1=X+1`, `t2=Z-1`, `t3=Y-1`) and the output from the subroutine `F(t)` must both be within the signed 16-bit range.
2.  The final sum `R` must also be within the signed 16-bit range.

#### 1. Constraints on Subroutine Input `t`
By solving for `t` in the inequalities `-32768 <= F(t) <= 32767` for each piece of the function, we find the combined valid input range for `t`:
-   **`t` must be in the range `[-10953, 10891]`**.

Applying this to the main program's arguments:
-   **Constraint on X:** `-10953 <= X + 1 <= 10891`  =>  **`-10954 <= X <= 10890`**
-   **Constraint on Y:** `-10953 <= Y - 1 <= 10891`  =>  **`-10952 <= Y <= 10892`**
-   **Constraint on Z:** `-10953 <= Z - 1 <= 10891`  =>  **`-10952 <= Z <= 10892`**

#### 2. Constraints on Final Result `R`
-   The minimum possible output of `F(t)` is `F(-10953) = -32766`.
-   The maximum possible output of `F(t)` is `F(10891) = 32766`.
-   The theoretical range for `R` is approximately `3 * Min(F)` to `3 * Max(F)`, which is `[-98298, 98298]`. This far exceeds the `[-32768, 32767]` limit.

**Final ODZ Statement:**
For the program to run without overflow:
1.  `X`, `Y`, and `Z` must satisfy their individual constraints listed above.
2.  The specific combination of `X`, `Y`, and `Z` must be chosen such that the final sum `R = F(X + 1) + F(Z - 1) + F(Y - 1)` falls within the range `[-32768, 32767]`.

## Execution Trace

The full execution trace is provided in the report, detailing the state of the IP, CR, AR, DR, SP, BR, AC, and PS registers for every instruction executed. The trace correctly shows:
-   The stack pointer (`SP`) decreasing with `PUSH` and increasing with `POP` and `RET`.
-   The `CALL` instruction saving the return address on the stack.
-   The `RET` instruction restoring the IP from the stack.
-   The conditional jumps (`BEQ`, `BPL`, `BMI`) correctly navigating the logic within the subroutine.
-   The final calculated value being stored in the result address `058`.

## Conclusion

During the execution of this laboratory work, I learned to work with subroutines, branching, and loops within the basic computer (БЭВМ) architecture. I studied the use of the stack for passing arguments and managing return addresses with `CALL` and `RET` instructions. This assignment provided practical experience in analyzing assembly code with modular components and complex control flow, reinforcing the understanding of how high-level function calls are implemented at the machine level.
