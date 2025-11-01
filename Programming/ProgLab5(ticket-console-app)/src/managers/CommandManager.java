package managers;

import commands.Command;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Uygulamadaki komutları ve komut geçmişini yöneten sınıf.
 */
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public void addToHistory(String command) {
        commandHistory.add(command);
        if(commandHistory.size() > 5) {
            commandHistory.remove(0);
        }
    }

    public List<String> getCommandHistory() {
        return commandHistory;
    }
}