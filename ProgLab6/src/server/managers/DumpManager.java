package server.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import common.models.Ticket;
import common.utility.LocalDateAdapter;
import common.utility.ZonedDateTimeAdapter;


import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;

/**
 * Koleksiyonu JSON formatında dosyaya yazan ve dosyadan okuyan sınıf (Sunucu tarafı).
 * Konsol bağımlılığı kaldırıldı, hata loglaması eklenecek.
 */
public class DumpManager {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeAdapter())
            .create();
    private final String fileName;

    public DumpManager(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Koleksiyonu dosyaya yazar. Hata durumunda System.err'e yazar.
     * @param collection Kaydedilecek koleksiyon.
     */
    public void writeCollection(LinkedHashMap<Long, Ticket> collection) {
        // logger.info("Koleksiyon dosyaya kaydediliyor: {}", fileName); // Loglama

        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(collection, writer);
            // logger.info("Koleksiyon başarıyla dosyaya kaydedildi."); // Loglama
        } catch (IOException e) {
            // logger.error("Dosyaya yazma hatası: {}", e.getMessage()); // Loglama
            System.err.println("ERROR: Failed to save collection to file: " + e.getMessage());
        } catch (Exception e) {
            // logger.error("Koleksiyon kaydedilirken beklenmedik hata:", e);
            System.err.println("ERROR: An unexpected error occurred while saving the collection: " + e.getMessage());
        }
    }


    /**
     * Koleksiyonu dosyadan okur. Hata durumunda boş koleksiyon döner ve hatayı System.err'e yazar.
     * @return Okunan koleksiyon veya hata durumunda boş koleksiyon.
     */

    public LinkedHashMap<Long, Ticket> readCollection() {
        // logger.info("Koleksiyon dosyadan okunuyor: {}", fileName); // Loglama
        File file = new File(fileName);
        if (!file.exists()) {
            // logger.warn("Koleksiyon dosyası bulunamadı: {}", fileName);
            System.err.println("ERROR: Collection file not found: " + fileName + ". Starting with an empty collection.");
            return new LinkedHashMap<>();
        }
        if (!file.canRead()) {
            // logger.error("Koleksiyon dosyası okuma izni yok: {}", fileName);
            System.err.println("ERROR: Unable to read collection file (no permission): " + fileName + ". Starting with an empty collection.");
            return new LinkedHashMap<>();
        }

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName))) {
            Type type = new TypeToken<LinkedHashMap<Long, Ticket>>(){}.getType();
            LinkedHashMap<Long, Ticket> col = gson.fromJson(reader, type);
            if(col == null) {
                //logger.warn("Dosyadan okunan koleksiyon null, boş koleksiyon oluşturuluyor.");
                System.err.println("ERROR: Collection read from file is null, creating empty collection.");
                col = new LinkedHashMap<>();
            }
            System.out.println("Collection loaded successfully from the file.");
            return col;
        } catch (FileNotFoundException e) {
            // logger.error("Dosya bulunamadı (beklenmedik): {}", fileName);
            System.err.println("File not found: " + fileName);
        } catch (IOException e) {
            // logger.error("Dosya okuma hatası:", e);
            System.err.println("File read error: " + e.getMessage());
        } catch (JsonSyntaxException | NumberFormatException e) {
            // logger.error("JSON format hatası:", e);
            System.err.println("JSON syntax error in file: " + fileName + " please fix it.");
            //System.exit(1);
        } catch (Exception e) {
            // logger.error("Koleksiyon okunurken beklenmedik hata:", e);
            System.err.println("ERROR: An unexpected error occurred while reading the collection: " + e.getMessage());
            //System.exit(1);
        }
        // Hata durumunda boş koleksiyon döndür
        return new LinkedHashMap<>();
    }
}