package classes;

import interfaces.Publishable;

class Newspapers extends Essence implements Publishable {
    public Newspapers(String name) {
        super(name);
    }

    @Override
    public void publish(String type, String content) {
        System.out.println(getName() + " published a " + type + ": " + content);
    }
    

    @Override
    public String toString() {
        return "Newspapers{" + super.toString() + "}";
    }
}
