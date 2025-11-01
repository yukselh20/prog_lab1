package classes;

abstract class Essence {
    private String name;

    public Essence(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Name: " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Essence essence = (Essence) obj;
        return name.equals(essence.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
