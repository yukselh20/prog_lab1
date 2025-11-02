# Database Lab 2: Advanced SQL Queries on the "Educational Process" Schema

This repository contains the SQL solutions for the second laboratory assignment in the "Databases" course. The focus of this assignment is to perform complex data retrieval from the "Educational Process" database schema. The work involves writing sophisticated queries using advanced SQL features like various JOIN types, aggregate functions (`AVG`, `MIN`, `COUNT`), subqueries (including correlated subqueries with `IN` and `EXISTS`), and result set grouping (`GROUP BY`, `HAVING`).

## Table of Contents
- [Assignment Description (Variant #13)](#assignment-description-variant-13)
- [SQL Query Implementations](#sql-query-implementations)
  - [Query 1: Filtered Join on Statement Types](#query-1-filtered-join-on-statement-types)
  - [Query 2: Chained RIGHT JOINs with Multiple Filters](#query-2-chained-right-joins-with-multiple-filters)
  - [Query 3: Check for Students Without a Tax ID (INN)](#query-3-check-for-students-without-a-tax-id-inn)
  - [Query 4: Find Groups with More Than 5 Students in a Department](#query-4-find-groups-with-more-than-5-students-in-a-department)
  - [Query 5: Compare Average Age Across Groups Using a Subquery](#query-5-compare-average-age-across-groups-using-a-subquery)
  - [Query 6: Find Expelled Students Using a Correlated Subquery (EXISTS)](#query-6-find-expelled-students-using-a-correlated-subquery-exists)
  - [Query 7: Find Students with Identical Names but Different IDs](#query-7-find-students-with-identical-names-but-different-ids)
- [Conclusion](#conclusion)

## Assignment Description (Variant #13)

The assignment requires writing and executing seven distinct SQL queries against the "Educational Process" database. Each query has specific requirements regarding the tables to be used, the attributes to be displayed, the filtering conditions to be applied, and the type of JOIN to be implemented.

## SQL Query Implementations

Below are the detailed solutions for each of the seven required queries.

### Query 1: Filtered Join on Statement Types

-   **Objective:** Retrieve the statement name (`НАИМЕНОВАНИЕ`) and person ID (`ЧЛВК_ИД`) from the `Н_ТИПЫ_ВЕДОМОСТЕЙ` and `Н_ВЕДОМОСТИ` tables.
-   **Conditions:** The statement name must be alphabetically less than 'Экзаменационный лист', AND the statement date must be before '1998-01-05'.
-   **Join Type:** `INNER JOIN`.

#### SQL Solution
```sql
SELECT
    Н_ТИПЫ_ВЕДОМОСТЕЙ.НАИМЕНОВАНИЕ,
    Н_ВЕДОМОСТИ.ЧЛВК_ИД
FROM
    Н_ТИПЫ_ВЕДОМОСТЕЙ
INNER JOIN
    Н_ВЕДОМОСТИ ON Н_ТИПЫ_ВЕДОМОСТЕЙ.ИД = Н_ВЕДОМОСТИ.ТВ_ИД
WHERE
    Н_ТИПЫ_ВЕДОМОСТЕЙ.НАИМЕНОВАНИЕ < 'Экзаменационный лист'
    AND Н_ВЕДОМОСТИ.ДАТА < '1998-01-05';```
**Explanation:** An `INNER JOIN` is used to combine records from both tables where the statement type ID matches. The `WHERE` clause then filters these combined results based on the two specified conditions.

---

### Query 2: Chained RIGHT JOINs with Multiple Filters

-   **Objective:** Retrieve the person's first name (`ИМЯ`), person ID (`ЧЛВК_ИД`), and study start date (`НАЧАЛО`) from the `Н_ЛЮДИ`, `Н_ОБУЧЕНИЯ`, and `Н_УЧЕНИКИ` tables.
-   **Conditions:** Patronymic is less than 'Сергеевич', grade book number (`НЗК`) is greater than 999080, AND group number is 3100.
-   **Join Type:** `RIGHT JOIN`.

#### SQL Solution
```sql
SELECT
    Н_ЛЮДИ.ИМЯ,
    Н_ОБУЧЕНИЯ.ЧЛВК_ИД,
    Н_УЧЕНИКИ.НАЧАЛО
FROM
    Н_ЛЮДИ
RIGHT JOIN
    Н_ОБУЧЕНИЯ ON Н_ЛЮДИ.ИД = Н_ОБУЧЕНИЯ.ЧЛВК_ИД
RIGHT JOIN
    Н_УЧЕНИКИ ON Н_ОБУЧЕНИЯ.ЧЛВК_ИД = Н_УЧЕНИКИ.ЧЛВК_ИД
WHERE
    Н_ЛЮДИ.ОТЧЕСТВО < 'Сергеевич'
    AND Н_ОБУЧЕНИЯ.НЗК > '999080'
    AND Н_УЧЕНИКИ.ГРУППА = '3100';
```
**Explanation:** This query uses chained `RIGHT JOIN`s to ensure all records from `Н_УЧЕНИКИ` are included, even if they don't have matching records in the other tables. The `WHERE` clause then filters the results based on three conditions across all three joined tables.

---

### Query 3: Check for Students Without a Tax ID (INN)

-   **Objective:** Find out if there are any students in group 3102 who do not have a registered Tax ID (`ИНН`).
-   **Key Technique:** `IS NULL` check.

#### SQL Solution
```sql
SELECT
    Н_ЛЮДИ.ИД,
    Н_ЛЮДИ.ФАМИЛИЯ,
    Н_ЛЮДИ.ИНН,
    Н_УЧЕНИКИ.ГРУППА
FROM
    Н_ЛЮДИ
JOIN
    Н_УЧЕНИКИ ON Н_ЛЮДИ.ИД = Н_УЧЕНИКИ.ЧЛВК_ИД
WHERE
    Н_УЧЕНИКИ.ГРУППА = '3102'
    AND Н_ЛЮДИ.ИНН IS NULL;
```
**Explanation:** The query joins the `Н_ЛЮДИ` and `Н_УЧЕНИКИ` tables, filters for students in group 3102, and then uses `AND Н_ЛЮДИ.ИНН IS NULL` to select only those records where the tax ID field is empty.

---

### Query 4: Find Groups with More Than 5 Students in a Department

-   **Objective:** Identify academic groups that had more than 5 students enrolled in the "Computational Technology" department during the year 2011.
-   **Key Techniques:** `GROUP BY`, `HAVING`, `COUNT`.

#### SQL Solution```sql
SELECT
    Н_УЧЕНИКИ.ГРУППА
FROM
    Н_УЧЕНИКИ
JOIN
    Н_ПЛАНЫ ON Н_УЧЕНИКИ.ПЛАН_ИД = Н_ПЛАНЫ.ИД
JOIN
    Н_ОТДЕЛЫ ON Н_ПЛАНЫ.ОТД_ИД = Н_ОТДЕЛЫ.ИД
WHERE
    Н_ОТДЕЛЫ.ИМЯ_В_ИМИН_ПАДЕЖЕ LIKE '%вычислительной техники%'
    AND Н_УЧЕНИКИ.НАЧАЛО < '2012-01-01'
    AND (Н_УЧЕНИКИ.КОНЕЦ IS NULL OR Н_УЧЕНИКИ.КОНЕЦ >= '2011-01-01')
GROUP BY
    Н_УЧЕНИКИ.ГРУППА
HAVING
    COUNT(*) > 5;
```
**Explanation:** The query joins three tables to link students to their departments. It filters for the correct department and for students active in 2011. The results are then grouped by `ГРУППА`, and the `HAVING` clause filters these groups to include only those with a student count greater than 5.

---

### Query 5: Compare Average Age Across Groups Using a Subquery

-   **Objective:** Display a table of all groups and their average student age, but only for groups where the average age is less than the minimum age of any student in group 3100.
-   **Key Technique:** Subquery within a `HAVING` clause.

#### SQL Solution
```sql
SELECT
    Н_УЧЕНИКИ.ГРУППА,
    ROUND(AVG(EXTRACT(YEAR FROM AGE(Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ))), 2) AS СРЕДНИЙ_ВОЗРАСТ
FROM
    Н_УЧЕНИКИ
JOIN
    Н_ЛЮДИ ON Н_УЧЕНИКИ.ЧЛВК_ИД = Н_ЛЮДИ.ИД
WHERE
    Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ IS NOT NULL
    AND Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ <= CURRENT_DATE
GROUP BY
    Н_УЧЕНИКИ.ГРУППА
HAVING
    AVG(EXTRACT(YEAR FROM AGE(Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ))) < (
        SELECT MIN(EXTRACT(YEAR FROM AGE(Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ)))
        FROM Н_УЧЕНИКИ
        JOIN Н_ЛЮДИ ON Н_УЧЕНИКИ.ЧЛВК_ИД = Н_ЛЮДИ.ИД
        WHERE Н_УЧЕНИКИ.ГРУППА = '3100'
        AND Н_ЛЮДИ.ДАТА_РОЖДЕНИЯ <= CURRENT_DATE
    );
```
**Explanation:** The main query calculates the average age for each group. The `HAVING` clause filters these groups by comparing their average age to the result of a subquery. The subquery itself calculates the minimum age found specifically within group 3100.

---

### Query 6: Find Expelled Students Using a Correlated Subquery (EXISTS)

-   **Objective:** Get a list of "Software Engineering" students from full-time or part-time programs who were officially expelled on September 1, 2012.
-   **Key Technique:** Correlated subquery with `EXISTS`.

#### SQL Solution
```sql
SELECT
    Н_УЧЕНИКИ.ГРУППА,
    Н_УЧЕНИКИ.ИД AS НомерСтудента,
    Н_ЛЮДИ.ФАМИЛИЯ,
    Н_ЛЮДИ.ИМЯ,
    Н_ЛЮДИ.ОТЧЕСТВО,
    Н_УЧЕНИКИ.П_ПРКOK_ИД AS НомерПунктаПриказа
FROM
    Н_УЧЕНИКИ
JOIN
    Н_ЛЮДИ ON Н_УЧЕНИКИ.ЧЛВК_ИД = Н_ЛЮДИ.ИД
WHERE
    Н_УЧЕНИКИ.КОНЕЦ = '2012-09-01'
    AND Н_УЧЕНИКИ.ПРИЗНАК = 'отчисл'
    AND EXISTS (
        SELECT 1
        FROM Н_ОБУЧЕНИЯ
        JOIN Н_ВИДЫ_ОБУЧЕНИЯ ON Н_ОБУЧЕНИЯ.ВИД_ОБУЧ_ИД = Н_ВИДЫ_ОБУЧЕНИЯ.ИД
        JOIN Н_ПЛАНЫ ON Н_УЧЕНИКИ.ПЛАН_ИД = Н_ПЛАНЫ.ИД
        JOIN Н_НАПР_СПЕЦ ON Н_ПЛАНЫ.НАПС_ИД = Н_НАПР_СПЕЦ.ИД
        WHERE
            Н_ОБУЧЕНИЯ.ЧЛВК_ИД = Н_УЧЕНИКИ.ЧЛВК_ИД
            AND Н_ВИДЫ_ОБУЧЕНИЯ.НАИМЕНОВАНИЕ IN ('Основное образование', 'Второе образование')
            AND Н_НАПР_СПЕЦ.НАИМЕНОВАНИЕ = 'Программная инженерия'
    );
```
**Explanation:** The outer query selects students based on their expulsion date and status. For each of these students, the `EXISTS` clause runs a correlated subquery that checks if the student is enrolled in the "Software Engineering" major under the specified forms of study. The outer query's row is only included if the subquery returns at least one matching row.

---

### Query 7: Find Students with Identical Names but Different IDs

-   **Objective:** Display a list of students who share the same first name but have different student IDs.
-   **Key Technique:** Subquery with `IN` and `GROUP BY`/`HAVING`.

#### SQL Solution
```sql
SELECT
    Н_ЛЮДИ.ИД,
    Н_ЛЮДИ.ИМЯ
FROM
    Н_ЛЮДИ
WHERE
    Н_ЛЮДИ.ИМЯ IN (
        SELECT Н_ЛЮДИ.ИМЯ
        FROM Н_ЛЮДИ
        GROUP BY Н_ЛЮДИ.ИМЯ
        HAVING COUNT(*) > 1
    )
ORDER BY
    Н_ЛЮДИ.ИМЯ, Н_ЛЮДИ.ИД;
```
**Explanation:** This query works in two stages. The inner subquery first identifies all first names that appear more than once in the `Н_ЛЮДИ` table. The outer query then retrieves the ID and first name of every person whose name is present in the list generated by the subquery. The results are ordered by name and then ID for easy comparison.

## Conclusion

During this lab, I became familiar with the main functions of the SQL language and the PostgreSQL dialect. I learned to write queries to retrieve, aggregate, filter, and sort data using various syntactical constructs of the language. As a result, I have mastered the DML (Data Manipulation Language) portion of SQL, which is essential for working with data stored within a relational database.
