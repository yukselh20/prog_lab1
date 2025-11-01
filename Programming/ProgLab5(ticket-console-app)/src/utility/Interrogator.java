package utility;

import java.util.Scanner;

/**
 * Kullanıcı girdisi modunu (interaktif veya script) kontrol eden yardımcı sınıf.
 */
public class Interrogator {
    private static Scanner userScanner;
    private static boolean fileMode = false;

    public static Scanner getUserScanner() {
        return userScanner;
    }
    public static void setUserScanner(Scanner scanner) {
        userScanner = scanner;
    }
    public static boolean fileMode() {
        return fileMode;
    }
    public static void setUserMode() {
        fileMode = false;
    }
    public static void setFileMode() {
        fileMode = true;
    }
}