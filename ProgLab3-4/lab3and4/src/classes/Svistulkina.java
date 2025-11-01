package classes;

import enums.PersonStatus;
import interfaces.Dissapearable;

public class Svistulkina extends Person implements Dissapearable {
    public Svistulkina() {
        super("Свистулькина", PersonStatus.MISSING);
    }

    @Override
    public void disappear() {
        System.out.println(getName() + " has disappeared!");
    }

    public void checkExistence(boolean exists) {
        if (!exists) {
            setStatus(PersonStatus.DOES_NOT_EXIST);
            System.out.println(getName() + " is now considered non-existent!");
        } else {
            System.out.println(getName() + " is still missing but might exist.");
        }
    }
    

    @Override
    public String toString() {
        return "Свистулькина{" + super.toString() + "}";
    }
}
