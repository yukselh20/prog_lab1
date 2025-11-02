# Database Lab 4: SQL Query Optimization and Indexing

This repository contains the solution for the fourth laboratory assignment in the "Databases" course. This assignment builds upon the queries developed in Lab 2, with a focus on performance analysis and optimization within a PostgreSQL environment. The primary objective is to analyze query execution plans, propose and implement effective indexing strategies, and verify the performance improvements using the `EXPLAIN ANALYZE` command.

## Table of Contents
- [Assignment Description](#assignment-description)
- [Query Optimization Methodology](#query-optimization-methodology)
- [Analysis of Query 1: Filtered Join on Statement Types](#analysis-of-query-1-filtered-join-on-statement-types)
  - [Objective & SQL](#objective--sql)
  - [Execution Plan Analysis (Without Indexes)](#execution-plan-analysis-without-indexes)
  - [Optimal Plan](#optimal-plan)
  - [Indexing Strategy](#indexing-strategy)
  - [Impact of Indexing & `EXPLAIN ANALYZE`](#impact-of-indexing--explain-analyze)
- [Analysis of Query 2: Chained RIGHT JOINs with Multiple Filters](#analysis-of-query-2-chained-right-joins-with-multiple-filters)
  - [Objective & SQL](#objective--sql-1)
  - [Execution Plan Analysis (Without Indexes)](#execution-plan-analysis-without-indexes-1)
  - [Optimal Plan](#optimal-plan-1)
  - [Indexing Strategy](#indexing-strategy-1)
  - [Impact of Indexing & `EXPLAIN ANALYZE`](#impact-of-indexing--explain-analyze-1)
- [Conclusion](#conclusion)

## Assignment Description

For two specific queries from a previous lab (Variant #13), the following tasks were required:
1.  **Propose Indexes:** For each query, suggest indexes that would reduce execution time. Specify the tables, columns, and index types, and justify why the proposed indexes would be beneficial.
2.  **Analyze Execution Plans:** Assuming no indexes exist initially, draw possible execution plans for the queries. From these plans, select the optimal one and explain the choice.
3.  **Evaluate Index Impact:** Describe how the execution plan would change after the proposed indexes are added.
4.  **Provide Proof:** For each query, include the output of the `EXPLAIN ANALYZE` command to demonstrate the actual execution plan chosen by PostgreSQL.

## Query Optimization Methodology

The analysis follows a standard query optimization approach:
1.  **Execution Plan Generation:** PostgreSQL's query planner generates multiple potential execution plans. We analyze two logical extremes: joining all data before filtering versus filtering each table before joining.
2.  **Optimal Plan Selection:** The most efficient plan is typically one that reduces the size of the intermediate data sets as early as possible. This strategy, known as **Predicate Pushdown**, minimizes I/O and CPU load during join operations.
3.  **Indexing:** Indexes provide fast, direct access to rows based on column values, allowing the database to avoid full table scans (`Sequential Scan`) in favor of much faster `Index Scans`. **B-Tree indexes** are ideal for range (`<`, `>`) and equality (`=`) comparisons, which are common in `WHERE` clauses and `JOIN` conditions.

---

## Analysis of Query 1: Filtered Join on Statement Types

### Objective & SQL

-   **Objective:** Retrieve the statement name and person ID for statements with a name alphabetically less than 'Экзаменационный лист' and a date before '1998-01-05'.
-   **Join Type:** `INNER JOIN`.

```sql
SELECT
    tv.НАИМЕНОВАНИЕ,
    v.ЧЛВК_ИД
FROM
    Н_ТИПЫ_ВЕДОМОСТЕЙ tv
INNER JOIN
    Н_ВЕДОМОСТИ v ON tv.ИД = v.ТВ_ИД
WHERE
    tv.НАИМЕНОВАНИЕ < 'Экзаменационный лист'
    AND v.ДАТА < '1998-01-05';
```

### Execution Plan Analysis (Without Indexes)

-   **Plan 1 (Inefficient):** First, perform a full `INNER JOIN` on `Н_ТИПЫ_ВЕДОМОСТЕЙ` and `Н_ВЕДОМОСТИ`. Then, apply the `WHERE` clause filters to the large, combined result set. This is inefficient due to the large volume of data processed during the join.
-   **Plan 2 (Efficient - Predicate Pushdown):** First, filter `Н_ТИПЫ_ВЕДОМОСТЕЙ` based on `НАИМЕНОВАНИЕ`. In parallel, filter `Н_ВЕДОМОСТИ` based on `ДАТА`. Then, join the two much smaller, pre-filtered result sets.

### Optimal Plan
**Plan 2** is optimal because filtering before joining significantly reduces the number of rows involved in the join operation, decreasing system load and execution time.

### Indexing Strategy

1.  **For table `Н_ТИПЫ_ВЕДОМОСТЕЙ`:**
    -   **On column `НАИМЕНОВАНИЕ`:**
        -   `CREATE INDEX idx_tv_naimenovanie ON Н_ТИПЫ_ВЕДОМОСТЕЙ USING BTREE (НАИМЕНОВАНИЕ);`
        -   **Type:** `BTREE`.
        -   **Justification:** To efficiently handle the range filter (`< 'Экзаменационный лист'`).
    -   **On column `ИД`:** A B-Tree index (the primary key `ТВ_РК`) already exists and is used for the `JOIN`.

2.  **For table `Н_ВЕДОМОСТИ`:**
    -   **On column `ДАТА`:**
        -   `CREATE INDEX idx_v_data ON Н_ВЕДОМОСТИ USING BTREE (ДАТА);` (An index `ВЕД_ДАТА_I` already exists).
        -   **Type:** `BTREE`.
        -   **Justification:** To speed up the range filter (`< '1998-01-05'`).
    -   **On column `ТВ_ИД`:** A B-Tree index (`ВЕД_ТВ_FK_I`) already exists and is used for the `JOIN`.

### Impact of Indexing & `EXPLAIN ANALYZE`

With indexes, the query planner will replace slow `Sequential Scans` with fast `Index Scans`. The `JOIN` operation can then leverage these indexed lookups, likely resulting in a highly efficient `Index Nested Loop Join` or `Merge Join`.

<details>
<summary>Click to view EXPLAIN ANALYZE output for Query 1</summary>

```
QUERY PLAN
----------------------------------------------------------------------------------------------------------------------------------------------------------------
 Nested Loop  (cost=0.29..8.24 rows=1 width=422) (actual time=0.027..0.028 rows=0 loops=1)
   Join Filter: (tv."ИД" = v."ТВ_ИД")
   ->  Seq Scan on "Н_ТИПЫ_ВЕДОМОСТЕЙ" tv  (cost=0.00..1.04 rows=1 width=422) (actual time=0.018..0.020 rows=2 loops=1)
         Filter: (("НАИМЕНОВАНИЕ")::text < 'Экзаменационный лист'::text)
         Rows Removed by Filter: 1
   ->  Index Scan using "ВЕД_ДАТА_I" on "Н_ВЕДОМОСТИ" v  (cost=0.29..7.19 rows=1 width=8) (actual time=0.002..0.002 rows=0 loops=2)
         Index Cond: ("ДАТА" < '1998-01-05 00:00:00'::timestamp without time zone)
 Planning Time: 0.226 ms
 Execution Time: 0.054 ms
(9 rows)
```
</details>

**Analysis of Output:** The planner correctly used an `Index Scan` on `Н_ВЕДОМОСТИ` via the existing `ВЕД_ДАТА_I` index. It performed a `Seq Scan` on `Н_ТИПЫ_ВЕДОМОСТЕЙ` because the table is very small, making a full scan faster than an index lookup. The final join was a `Nested Loop`, which is efficient here.

---

## Analysis of Query 2: Chained RIGHT JOINs with Multiple Filters

### Objective & SQL
-   **Objective:** Retrieve student details by joining `Н_ЛЮДИ`, `Н_ОБУЧЕНИЯ`, and `Н_УЧЕНИКИ`.
-   **Join Type:** `RIGHT JOIN`.

```sql
SELECT
    L.ИМЯ,
    О.ЧЛВК_ИД,
    U.НАЧАЛО
FROM
    Н_ЛЮДИ L
RIGHT JOIN
    Н_ОБУЧЕНИЯ O ON L.ИД = O.ЧЛВК_ИД
RIGHT JOIN
    Н_УЧЕНИКИ U ON O.ЧЛВК_ИД = U.ЧЛВК_ИД
WHERE
    L.ОТЧЕСТВО < 'Сергеевич'
    AND O.НЗК > '999080'
    AND U.ГРУППА = '3100';
```

### Execution Plan Analysis (Without Indexes)

-   **Plan 1 (Inefficient):** Perform the chained `RIGHT JOIN`s first, creating a very large intermediate table. Then, apply the three `WHERE` filters to this result set.
-   **Plan 2 (Efficient - Predicate Pushdown):** Filter each of the three tables independently based on the `WHERE` conditions. Then, join the much smaller, pre-filtered results. **Note:** The presence of `WHERE` conditions on all tables allows the query planner to treat the `RIGHT JOIN`s as more efficient `INNER JOIN`s, as rows with `NULL`s from the joins would be eliminated by the filters anyway.

### Optimal Plan
**Plan 2** is vastly superior. Filtering before joining drastically reduces the computational cost and memory usage of the query.

### Indexing Strategy

1.  **For table `Н_ЛЮДИ`:**
    -   **On column `ОТЧЕСТВО`:** `CREATE INDEX idx_l_otchestvo ON Н_ЛЮДИ USING BTREE (ОТЧЕСТВО);`
    -   **Justification:** To accelerate the range filter on patronymic.
2.  **For table `Н_ОБУЧЕНИЯ`:**
    -   **On column `НЗК`:** `CREATE INDEX idx_o_nzk ON Н_ОБУЧЕНИЯ USING BTREE (НЗК);`
    -   **Justification:** To accelerate the range filter on grade book number.
3.  **For table `Н_УЧЕНИКИ`:**
    -   **On column `ГРУППА`:** `CREATE INDEX idx_u_gruppa ON Н_УЧЕНИКИ USING BTREE (ГРУППА);`
    -   **Justification:** To accelerate the equality filter on group number.

### Impact of Indexing & `EXPLAIN ANALYZE`
With indexes on all filter columns, the planner can use `Index Scans` to retrieve the small, relevant subsets from each table before performing the join operations. This leads to a significant reduction in execution time.

<details>
<summary>Click to view EXPLAIN ANALYZE output for Query 2</summary>

```
QUERY PLAN
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 Nested Loop  (cost=0.57..129.04 rows=1 width=25) (actual time=1.238..1.238 rows=0 loops=1)
   Join Filter: (u."ВИД_ОБУЧ_ИД" = o."ВИД_ОБУЧ_ИД")
   ->  Nested Loop  (cost=0.28..128.07 rows=1 width=25) (actual time=1.238..1.238 rows=0 loops=1)
         ->  Seq Scan on "Н_ОБУЧЕНИЯ" o  (cost=0.00..119.76 rows=1 width=8) (actual time=1.237..1.237 rows=0 loops=1)
               Filter: (("НЗК")::text > '999080'::text)
               Rows Removed by Filter: 5021
         ->  Index Scan using "ЧЛВК_РК" on "Н_ЛЮДИ" l  (cost=0.28..8.30 rows=1 width=17) (never executed)
               Index Cond: ("ИД" = o."ЧЛВК_ИД")
               Filter: (("ОТЧЕСТВО")::text < 'Сергеевич'::text)
   ->  Index Scan using "УЧЕН_ОБУЧ_FK_I" on "Н_УЧЕНИКИ" u  (cost=0.29..0.96 rows=1 width=16) (never executed)
         Index Cond: ("ЧЛВК_ИД" = l."ИД")
         Filter: (("ГРУППА")::text = '3100'::text)
 Planning Time: 0.833 ms
 Execution Time: 1.277 ms
(14 rows)
```
</details>

**Analysis of Output:** The planner chose a `Nested Loop` strategy. It started with a `Seq Scan` on `Н_ОБУЧЕНИЯ` and applied the filter. For matching rows, it planned to use `Index Scans` on the other two tables. Although the final query returned no rows (and the index scans were "never executed"), the plan demonstrates the optimizer's intent to use indexes where available to avoid full table scans.

## Conclusion

During this laboratory work, I became familiar with the use of indexes to accelerate query processing in SQL, as well as with query execution plans, their construction, and analysis. I learned to identify performance bottlenecks and propose effective indexing strategies to resolve them, verifying the results with practical tools like `EXPLAIN ANALYZE`.
