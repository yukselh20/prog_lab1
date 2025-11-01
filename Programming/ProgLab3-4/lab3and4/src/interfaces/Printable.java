package interfaces;

import exeptions.*;

public interface Printable {
    void printReport(String report);
    void printJoke(String joke) throws HumorOverdoseException;
}
