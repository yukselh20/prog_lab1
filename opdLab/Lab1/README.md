# OPD Lab Report 1: Unix/Linux Command Line Fundamentals

This repository contains the solution and report for the first laboratory assignment of the "Fundamentals of Professional Activities" (OPD) course at ITMO University. This work covers the execution of a series of file system operations using fundamental Unix/Linux commands, including: creating a directory tree, creating files with content, managing file permissions, copying and creating links, searching and filtering data, and finally, cleaning up the created structures.

| **Course**            | Fundamentals of Professional Activities |
| --------------------- | --------------------------------------- |
| **Lab No.**           | 1                                       |
| **Variant**           | 12230                                   |
| **Student**           | Hamza Yüksel                            |
| **Group**             | P3132                                   |
| **Instructor**        | M.A. Nyagin                             |

## Table of Contents
- [Task Description](#task-description)
- [Implementation Steps and Explanations](#implementation-steps-and-explanations)
  - [1. Creating Directories and Files](#1-creating-directories-and-files)
  - [2. Setting File Permissions](#2-setting-file-permissions)
  - [3. Creating Copies, Symbolic, and Hard Links](#3-creating-copies-symbolic-and-hard-links)
  - [4. Searching, Filtering, and Data Processing](#4-searching-filtering-and-data-processing)
  - [5. Deleting Files and Directories](#5-deleting-files-and-directories)
- [Conclusion](#conclusion)

## Task Description

The tasks specified for **Variant 12230** are as follows:

1.  **Create Directory Tree:** Create the directory and file tree specified in the variant, using a root directory named `lab0`. Use the following commands for creation and navigation: `mkdir`, `echo`, `cat`, `touch`, `ls`, `pwd`, `cd`, `cp`, `rm`, `rmdir`, `mv`.
2.  **Set Permissions:** Using the `chmod` command, set the required access rights (read, write, execute) for the specified files and directories, utilizing both octal (numeric) and symbolic notations.
3.  **Copy and Create Links:** Copy a part of the tree and create symbolic (soft) and hard links within the tree using the `cp` and `ln` commands. Use input/output redirection (`>`).
4.  **Search and Filter:** Use commands such as `cat`, `wc`, `ls`, `head`, `tail`, `echo`, `sort`, `grep`, and the pipe operator (`|`) to search within files and directories, filter their contents, and sort the data.
5.  **Deletion:** Use the `rm` and `rmdir` commands to remove the files, links, and directories specified in the variant from the system.

## Implementation Steps and Explanations

### 1. Creating Directories and Files

In the first step, the nested directory structure was created using the `mkdir` command. Subsequently, files with the specified content were created using `echo -e` and `cat >`.

-   **Directory creation commands:**
    ```bash
    mkdir lab0
    mkdir lab0/bisharp4
    mkdir lab0/bisharp4/togepi lab0/bisharp4/cinccino lab0/bisharp4/raticate
    mkdir lab0/torterra7 lab0/torterra7/metang lab0/torterra7/pineco lab0/torterra7/charmander
    mkdir lab0/venonat8 lab0/venonat8/tepig lab0/venonat8/skuntank
    ```
-   **File creation commands:**
    ```bash
    echo -e 'Тип покемона\tGROUND DRAGON\n' > lab0/bisharp4/flygon
    echo -e 'Способности\tMud-Slap\n...' > lab0/bisharp4/lairon
    echo -e 'Ходы\tAqua Tail Body Slam...' > lab0/bisharp4/furret
    # ...other files
    ```
-   **Final Structure Created (`ls -lR lab0`):**
    ```    lab0/
    ├── bisharp4
    │   ├── cinccino
    │   ├── drapion0
    │   ├── flygon
    │   ├── furret
    │   ├── lairon
    │   ├── raticate
    │   └── togepi
    ├── pidgeotto8
    ├── salamence0
    ├── torterra7
    │   ├── charmander
    │   ├── gulpin
    │   ├── metang
    │   ├── pineco
    │   └── torterra
    └── venonat8
        ├── gulpin
        ├── masquerain
        ├── shroomish
        ├── skuntank
        ├── tepig
        └── yanmega
    ```

### 2. Setting File Permissions

The `chmod` command modifies the access permissions (read `r`, write `w`, execute `x`) for the owner (`u`), group (`g`), and others (`o`). This was done using two methods:
-   **Numeric (Octal) Method:** A three-digit number is used, where `r=4`, `w=2`, `x=1` are summed for each category (e.g., `chmod 755 file`).
-   **Symbolic Method:** Uses the letters `u/g/o/a` (all) with the operators `+/-/=` (e.g., `chmod u+x,go-w file`).

All permission changes specified in the variant were applied using these two methods. For example:
```bash
chmod 524 lab0/bisharp4
chmod ug=r,o=- lab0/bisharp4/flygon
chmod 363 lab0/bisharp4/togepi
# ...other permissions
```
As a result of these changes, access to some files and directories was restricted, leading to `Permission denied` errors in subsequent steps. This served as confirmation that the permissions were working correctly.

### 3. Creating Copies, Symbolic, and Hard Links

This step utilized three fundamental methods of file system manipulation:
-   **Copying (`cp`):** Duplicates the content of a file or directory (with `-r`) to a different location with a new `inode`. It is independent of the original.
-   **Symbolic Link (`ln -s`):** A special file that contains the path to the original file. If the original is deleted, the link becomes "broken." It is indicated by `->` in the `ls -l` output.
-   **Hard Link (`ln`):** A second file name that shares the same `inode` as the original file. The file's data is not removed from the disk until all hard links to it are deleted.

-   **Example Commands:**
    ```bash
    # Creating a symbolic link
    ln -s /home/studs/s408078/lab0/bisharp4 Copy_41

    # Creating a hard link
    ln lab0/drapion0 lab0/bisharp4/furretdrapion

    # Copying a directory
    cp -r lab0/torterra7 lab0/bisharp4/raticate
    ```
Using the `ls -i` command, it was confirmed that the `drapion0` and `furretdrapion` files had the same `inode` number, verifying that they were hard-linked.

### 4. Searching, Filtering, and Data Processing

In this section, the pipeline operator (`|`) was used to direct the output of one command to the input of another, and the `2>` operator was used to redirect the standard error stream (`stderr`) to a file.
-   **Word Counting and Sorting:**
    ```bash
    # Count lines in files starting with 'p' and sort in reverse numerical order
    # Write errors to /tmp/errorH.txt
    wc -l lab0/p* lab0/*/p* 2> /tmp/errorH.txt | sort -nr
    ```
-   **Chaining List, Filter, and Sort Commands:**
    ```bash
    # List the lab0 directory, find lines containing 'on', get the last 3,
    # sort by the 2nd field in reverse alphabetical order, and redirect errors
    ls -lR lab0 2> /tmp/errorsH.txt | grep 'on' | tail -n 3 | sort -rk2
    ```
-   **Combining Standard Output and Error (`2>&1`):**
    ```bash
    # Concatenate contents of multiple files, sort in reverse,
    # and display both normal output and errors in the same stream
    cat lab0/torterra7/gulpin lab0/torterra7/torterra lab0/venonat8/yanmega | sort -r 2>&1
    ```
These commands demonstrate the Unix philosophy of combining small, focused tools to accomplish complex tasks.

### 5. Deleting Files and Directories

In the final step, a portion of the created structure was cleaned up using the `rm` (remove files/links) and `rmdir` (remove empty directories) commands.
-   Due to restricted permissions, `Permission denied` errors were encountered when trying to delete some files.
-   To overcome this, the necessary permissions (e.g., `chmod 777` or `chmod u+w`) were temporarily granted using `chmod` before deletion.
-   The `rm -r` command was used to delete a directory and its contents recursively.

```bash
# Fixing permissions before deletion
chmod 777 lab0/pidgeotto8

# Deleting a file
rm lab0/pidgeotto8

# Deleting a directory and its contents
rm -r lab0/bisharp4
```

## Conclusion

This laboratory exercise provided hands-on experience with fundamental file system management skills on a Unix/Linux command-line interface (CLI). The following core competencies were acquired:
-   Creating complex directory and file hierarchies using `mkdir`, `echo`, and `cat`.
-   Understanding and managing file access permissions with `chmod` using both numeric and symbolic methods.
-   Learning the theoretical and practical differences between copies, symbolic links, and hard links using `cp`, `ln -s`, and `ln`.
-   Building powerful command chains with text processing tools like `grep`, `sort`, `wc`, `tail`, combined with the pipeline (`|`) and I/O redirection (`>`, `2>`) operators.
-   Safely cleaning up the file system using `rm` and `rmdir`.

Encountering and resolving errors like `Permission denied` provided a practical understanding of the importance of file permissions and the problem-solving process involved in system administration tasks.
