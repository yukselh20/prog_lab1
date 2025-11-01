package client.utility.console;

/**
 * Konsol için standart uygulama.
 */
public class StandardConsole implements Console {
    private static final String PS1 = "$ ";

    @Override
    public void print(Object obj) {
        System.out.print(obj);
    }

    @Override
    public void println(Object obj) {
        System.out.println(obj);
    }
    @Override
    public void printError(Object obj) {
        System.out.println("mistake: " + obj);
    }

    @Override
    public void ps1() {
        print(PS1);
    }

    @Override
    public String getPS1() {
        return PS1;
    }

}
