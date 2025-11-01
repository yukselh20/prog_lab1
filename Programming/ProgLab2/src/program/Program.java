package prog_lab2;

import myPokemons.*;
import ru.ifmo.se.pokemon.Battle;

public class Program {

    public static void main(String[] args) {
        Battle b = new Battle();

        // Create Pokémon instances
        Petilil petilil = new Petilil("Legend", 1);
        Kyurem kyurem = new Kyurem("Precious", 1);
        Blissey blissey = new Blissey("Useless", 1);
        Chansey chansey = new Chansey("My Egg", 1);
        Happiny happiny = new Happiny("Happiness", 1);
        Lilligant lilligant = new Lilligant("Green Coconut", 1);

        // Add Pokémon to teams and count them
        int allyCount = 0;
        int foeCount = 0;

        b.addAlly(kyurem);
        allyCount++;

        b.addAlly(blissey);
        allyCount++;

        b.addAlly(happiny);
        allyCount++;

        // Check if both teams have at least one Pokémon
        if (allyCount == 0 || foeCount == 0) {
            System.err.println("Error: Both teams must have at least one Pokémon to start the battle.");
            return; // Stop execution if teams are not properly set up
        }

        // Start the battle
        b.go();
    }
}
