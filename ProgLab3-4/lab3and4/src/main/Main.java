package main;

import classes.*;
import java.util.Random;
import enums.DailyNewspaperTypes;
import enums.PersonStatus;

public class Main {
    public static void main(String[] args) {
        Random random = new Random();

        // Random initial status for Свистулькина
        PersonStatus initialStatus = random.nextBoolean() ? PersonStatus.MISSING : PersonStatus.DOES_NOT_EXIST;
        Svistulkina officer = new Svistulkina();
        officer.setStatus(initialStatus);
        System.out.println(officer.getName() + " is initially: " + officer.getStatus());

        // Randomly select a type of newspaper activity
        String[] articles = {
            "Officer Свистулькина is still missing.",
            "The police are unable to locate officer Свистулькина.",
            "Rumors suggest that officer Свистулькина never existed."
        };
        DailyNewspaperTypes[] types = DailyNewspaperTypes.values();
        int randomArticleIndex = random.nextInt(articles.length);
        DailyNewspaperTypes randomType = types[random.nextInt(types.length)];

        ReportNewspapers dailyReport = new ReportNewspapers("Daily News");
        dailyReport.publish(randomType.name(), articles[randomArticleIndex]);

        // Random joke creation
        JokingNewspapers humorPaper = new JokingNewspapers("Humor Daily");
        if (random.nextBoolean()) {
            humorPaper.addJoke("Why can't Свистулькина find himself? Because he doesn't exist!");
        } else {
            humorPaper.addFunnyStory("Свистулькина was last seen chasing his shadow!");
        }

        // Readers' random reactions
        Readers reader = new Readers("Jane Doe");
        if (random.nextBoolean()) {
            reader.discuss("Some newspapers ridicule the officer's existence.");
        } else {
            reader.speculate("Свистулькина", "might just be a ghost!");
        }

        // Police investigation with random outcomes
        Polices investigator = new Polices("Detective Smith");
        investigator.investigate("The case of the missing Свистулькина.");
        if (random.nextBoolean()) {
            System.out.println("The investigation found no trace of Свистулькина.");
        } else {
            System.out.println("The investigation suggests Свистулькина might still exist.");
        }

        // Update officer's status dynamically
        boolean exists = random.nextBoolean();
        officer.checkExistence(exists);
    }
}
