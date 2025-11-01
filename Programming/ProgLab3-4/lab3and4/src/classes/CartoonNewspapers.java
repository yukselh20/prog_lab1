package classes;

import interfaces.Illustratable;
import interfaces.Ridiculable;
import interfaces.Searchable;

class CartoonNewspapers extends Newspapers implements Illustratable, Ridiculable, Searchable {
    private String cartoon;
    private String lantern;

    public CartoonNewspapers(String name, String cartoon, String lantern) {
        super(name);
        this.cartoon = cartoon;
        this.lantern = lantern;
    }

    @Override
    public void illustrate(String description) {
        System.out.println(getName() + " illustrates: " + description);
    }

    @Override
    public void ridicule(String subject, String content) {
        System.out.println(getName() + " ridicules " + subject + ": " + content);
    }

    @Override
    public void search(String query) {
        System.out.println(getName() + " is searching for: " + query + " in the cartoon.");
    }

    @Override
    public String toString() {
        return "CartoonNewspaper{" + super.toString() + ", cartoon='" + cartoon + "', lantern='" + lantern + "'}";
    }
}