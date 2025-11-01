package classes;

import enums.PersonStatus;
import interfaces.Investigable;

public class Polices extends Person implements Investigable {
    public Polices(String name) {
        super(name, PersonStatus.ACTIVE);
    }

    @Override
    public void investigate(String caseDetails) {
        System.out.println(getName() + " is investigating: " + caseDetails);
    }

    @Override
    public String toString() {
        return "Polices{" + super.toString() + "}";
    }
}
