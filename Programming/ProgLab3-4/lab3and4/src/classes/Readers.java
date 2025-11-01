package classes;

import enums.PersonStatus;
import interfaces.Discussable;
import interfaces.Speculatable;

public class Readers extends Person implements Discussable, Speculatable {
    public Readers(String name) {
        super(name, PersonStatus.IDLE);
    }

    @Override
    public void discuss(String topic) {
        System.out.println(getName() + " is discussing: " + topic);
    }

    @Override
    public void speculate(String subject, String hypothesis) {
        System.out.println(getName() + " speculates about " + subject + ": " + hypothesis);
    }
    

    @Override
    public String toString() {
        return "Readers{" + super.toString() + "}";
    }
}