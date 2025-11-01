package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import models.Ticket;
import utility.LocalDateAdapter;
import utility.ZonedDateTimeAdapter;
import utility.console.Console;


import java.io.FileInputStream;//
import java.io.FileNotFoundException;
import java.io.FileWriter;//
import java.io.IOException;
import java.io.InputStreamReader; //
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

/**
 * Koleksiyonu JSON formatında dosyaya yazan ve dosyadan okuyan sınıf.
 */
public class DumpManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();
    private final String fileName;
    private final Console console;

    public DumpManager(String fileName, Console console) {
        this.fileName = fileName;
        this.console = console;
    }

    public void writeCollection(LinkedHashMap<Long, Ticket> collection) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(gson.toJson(collection));
            console.println("The collection has been successfully saved to a file!");
        } catch (IOException e) {
            console.printError("Error writing to a file: " + e.getMessage());
        }
    }

    public LinkedHashMap<Long, Ticket> readCollection() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName))) {
            Type type = new TypeToken<LinkedHashMap<Long, Ticket>>(){}.getType();
            LinkedHashMap<Long, Ticket> col = gson.fromJson(reader, type);
            if(col == null) {
                col = new LinkedHashMap<>();
            }
            console.println("The collection has been successfully uploaded!");
            return col;
        } catch (FileNotFoundException e) {
            console.printError("File not found: " + fileName);
        } catch (IOException e) {
            console.printError("File read error: " + e.getMessage());
        } catch (JsonSyntaxException e) {
            console.printError("JSON syntax error in file: " + fileName + " please fix it.");
            System.exit(1);
        } catch (NumberFormatException e) {
            console.printError("JSON syntax error in file: " + fileName + " please fix it.");
            System.exit(1);
        }
        return new LinkedHashMap<>();
    }
}