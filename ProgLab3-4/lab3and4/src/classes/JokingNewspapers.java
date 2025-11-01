package classes;

import java.util.ArrayList;
import java.util.List;
import exeptions.HumorOverdoseException;
import interfaces.Printable;
import interfaces.Speculatable;

public class JokingNewspapers extends Newspapers implements Printable, Speculatable {
    private List<String> jokes = new ArrayList<>();
    private List<String> funnyStories = new ArrayList<>();

    public JokingNewspapers(String name) {
        super(name);
    }

    public void addJoke(String joke) {
        jokes.add(joke);
        System.out.println(getName() + " added a joke: " + joke);
    }

    public void addFunnyStory(String story) {
        funnyStories.add(story);
        System.out.println(getName() + " added a funny story: " + story);
    }

    @Override
    public void printReport(String report) {
        throw new UnsupportedOperationException("Reports are not supported in Joking Newspapers.");
    }

    @Override
    public void printJoke(String joke) throws HumorOverdoseException {
        jokes.add(joke);
        System.out.println(getName() + " published a joke: " + joke);
        if (jokes.size() > 5) {
            throw new HumorOverdoseException("Too many jokes! Readers can't stop laughing. Total jokes: " + jokes.size());
        }
    }

    @Override
    public void speculate(String subject, String hypothesis) {
        System.out.println(getName() + " speculates that " + subject + ": " + hypothesis);
    }
    

    @Override
    public String toString() {
        return "JokingNewspapers{" + super.toString() + ", jokes=" + jokes + ", funnyStories=" + funnyStories + "}";
    }
}

