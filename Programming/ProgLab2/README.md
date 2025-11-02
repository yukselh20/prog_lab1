# Lab Assignment #2: Programming (Pokémon Battle)

This project is an implementation of a Pokémon battle simulator in Java, based on the principles of Object-Oriented Programming (OOP). The task involves creating classes for specific Pokémon and their attacks, managing their interactions in battle, and demonstrating inheritance and polymorphism.

## Assignment Variant (71263)

For this variant, the following Pokémon and their unique move sets were implemented:

*   **Kyurem**
    *   Draco Meteor (Special)
    *   Ice Beam (Special)
    *   Dragon Rage (Special)
    *   Swagger (Status)
*   **Petilil**
    *   Confide (Status)
    *   Double Team (Status)
    *   Rest (Status)
*   **Lilligant** (Evolution of Petilil)
    *   Confide (Status)
    *   Double Team (Status)
    *   Rest (Status)
    *   Petal Blizzard (Physical)
*   **Happiny**
    *   Dream Eater (Special)
    *   Swagger (Status)
*   **Chansey** (Evolution of Happiny)
    *   Dream Eater (Special)
    *   Swagger (Status)
    *   Present (Physical)
*   **Blissey** (Evolution of Chansey)
    *   Dream Eater (Special)
    *   Swagger (Status)
    *   Present (Physical)
    *   Double Slap (Physical)

## Project Structure

The project is organized using packages for better code structure:

*   **`myMoves`**: Contains the classes for all implemented attacks, separated by type.
    *   `phsicalMoves`: `DoubleSlap`, `PetalBlizzard`, `Present`.
    *   `specialMoves`: `DracoMeteor`, `DragonRage`, `DreamEater`, `IceBeam`.
    *   `statusMoves`: `Confide`, `DoubleTeam`, `Swagger`, `Rest`.
*   **`myPokemons`**: Contains the classes for each implemented Pokémon.
    *   `Blissey.java`, `Chansey.java`, `Happiny.java`, `Kyurem.java`, `Lilligant.java`, `Petilil.java`.
*   **`prog_lab2`**: The main package containing the primary class to run the simulation.
    *   `Program.java`: Creates Pokémon instances, assigns them to teams, and starts the battle.

## Implementation Details

### Inheritance and Hierarchy
*   All Pokémon classes (`Kyurem`, `Blissey`, etc.) inherit from the base class `ru.ifmo.se.pokemon.Pokemon`.
*   Evolution chains are implemented according to the assignment (Happiny -> Chansey -> Blissey and Petilil -> Lilligant).
*   Attack classes inherit from their respective base classes: `PhysicalMove`, `SpecialMove`, or `StatusMove`.

### Attack Logic
Unique effects were implemented for certain attacks by overriding methods from the base classes:
*   **Draco Meteor**: After dealing damage, it lowers the user's Special Attack by 2 stages (`applySelfEffects`).
*   **Ice Beam**: Has a 10% chance to freeze the target (`applyOppEffects`).
*   **Dream Eater**: Deals damage and heals the attacker (for 50% of the damage dealt), but only works if the target is asleep (`applyOppDamage`).
*   **Swagger**: Confuses the target but raises its Attack by 2 stages.
*   **Double Slap**: Hits 2 to 5 times in one turn.
*   **Rest**: The user falls asleep for 2 turns and fully restores its health.

### UML Diagram
To visualize the object model, a UML class diagram was created, which clearly demonstrates the inheritance hierarchy and the relationships between the Pokémon and attack classes.

## Build and Execution

The project depends on the provided `Pokemon.jar` library.

1.  **Compilation**: To compile the source code, the `Pokemon.jar` must be included in the classpath.
2.  **Archiving**: The compiled `.class` files are packaged into an executable `MyJar.jar` archive.
3.  **Execution**: The program is run on the `helios` server with the following command:
    ```bash
    java -jar MyJar.jar
    ```

## Execution Example
Below is a snippet of the program's output, demonstrating the battle flow:

```
[s408078@helios ~/lab2/build]$ java -jar MyJar.jar
Picked up _JAVA_OPTIONS: -XX:MaxHeapSize=1G -XX:MaxMetaspaceSize=128m
Kyurem Precious из команды полосатых вступает в бой!
Chansey My Egg из команды синих вступает в бой!
Kyurem Precious Using Swagger.
Kyurem Precious attack increased by 2 stage
Chansey My Egg Using Present.
Kyurem Precious теряет 5 здоровья.
Kyurem Precious Using DragonRage.
Chansey My Egg теряет 40 здоровья.
Chansey My Egg теряет сознание.
Petilil Legend из команды синих вступает в бой!
Kyurem Precious Using DracoMeteor.
Petilil Legend теряет 10 здоровья.
Kyurem Precious Special Attack -2
...
Lilligant Green Coconat теряет 40 здоровья.
Lilligant Green Coconat теряет сознание.
В команде синих не осталось покемонов.
Команда полосатых побеждает в этом бою!
[s408078@helios ~/lab2/build]$```

## Conclusion
As a result of this project, I learned how to use external `jar` files in my source code, which is an essential skill for working with libraries. In this lab, I created a simple battle simulation for my Pokémon and learned how to write methods and constructors. Thanks to this interactive example, I became acquainted with the fundamentals of OOP (Object-Oriented Programming), such as inheritance and polymorphism. Using the UML diagram, I was able to visualize the relationships between my classes and superclasses.
