package commands;

/**
 * Tüm komutların ortak özelliklerini tanımlayan soyut sınıf.
 */
public abstract class Command {
    private final String name;
    private final String description;

    public Command(String name, String description){
        this.name = name;
        this.description = description;
    }
    public String getName(){ return name; }
    public String getDescription(){ return description; }
    /**
     * Komutu çalıştırır.
     * @param arguments Komut argümanları.
     * @return Komutun başarılı çalışıp çalışmadığını belirten değer.
     */
    public abstract boolean apply(String[] arguments);
}
