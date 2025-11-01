package server.managers;

import common.models.Ticket;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.lang.*;

/**
 * Ticket nesnelerinin tutulduğu LinkedHashMap koleksiyonunu yöneten sınıf (Sunucu tarafı).
 */

public class CollectionManager {
    private LinkedHashMap<Long, Ticket> collection = new LinkedHashMap<>();
    private LocalDateTime lastInitTime;
    private LocalDateTime lastSaveTime;
    private final DumpManager dumpManager;

    public CollectionManager(DumpManager dumpManager) {
        this.dumpManager = dumpManager;
        loadCollection();
        //initializeKeys(); // Key değerlerini Ticket nesnelerine set et
    }

    /**
     * Koleksiyonu döndürür. Dışarıdan değiştirilmemesi için kopyası döndürülebilir
     * ancak Lab 6 kapsamında direkt referans yeterli olabilir. Senkronizasyon önemli.
     * @return Ticket koleksiyonu.
     */

    public synchronized LinkedHashMap<Long, Ticket> getCollection() {

        return collection;
    }

    /** Koleksiyonun türünü döndürür. */
    public String collectionType() {
        return collection.getClass().getName();
    }

    public int collectionSize() {
        return collection.size();
    }

    /**
     * Belirtilen ID'ye sahip Ticket'ı bulur.
     * @param id Aranacak ID.
     * @return Bulunan Ticket veya null.
     */
    public synchronized Ticket getById(int id) {
        return collection.values().stream()
                .filter(ticket -> ticket.getId() == id)
                .findFirst().orElse(null);
    }

    /**
     * Yeni bir Ticket nesnesini koleksiyona ekler.
     * ID ve CreationDate'in Ticket constructor'ında otomatik atandığı varsayılır.
     * @param key    Eklenecek anahtar.
     * @param ticket Eklenecek Ticket nesnesi (ID ve tarih hariç verilerle).
     * @return Ekleme başarılıysa true, değilse false (örn. key varsa).
     */

    public synchronized boolean addToCollection(Long key, Ticket ticket) {
         // Key değerini Ticket nesnesine ata
        if (collection.containsKey(key)) {
            return false;
        }

        collection.put(key, ticket);
        return true;
    }


    /**
     * Belirtilen anahtara sahip elemanı koleksiyondan siler.
     * @param key Silinecek anahtar.
     * @return Silme başarılıysa true, eleman yoksa false.
     */

    public synchronized boolean removeFromCollection(Long key) {
        return collection.remove(key) != null;
    }


    /**
     * Koleksiyonu temizler.
     */
    public synchronized void clearCollection() {
        collection.clear();
        Ticket.updateNextId(collection); // Veya Ticket.resetNextId();
    }


    /**
     * Koleksiyonu dosyaya kaydeder.
     */
    public synchronized void saveCollection() {
        // logger.info("Koleksiyon kaydediliyor...");
        dumpManager.writeCollection(collection);
        lastSaveTime = LocalDateTime.now();
    }


    /**
     * Koleksiyonu dosyadan yükler ve bir sonraki ID'yi ayarlar.
     */
    private synchronized void loadCollection() {
        // logger.info("Koleksiyon yükleniyor...");
        collection = dumpManager.readCollection();
        lastInitTime = LocalDateTime.now();
        // Koleksiyon yüklendikten sonra static ID üretecini güncelle
        Ticket.updateNextId(collection);
        // logger.info("Koleksiyon yüklendi. Boyut: {}. Sonraki ID: {}", collection.size(), Ticket.getNextId()); //getNextId gibi bir metot varsayımıyla
    }

    /**
     * Koleksiyon hakkında bilgi döndürür.
     * @return Koleksiyon bilgisi.
     */
    public String getCollectionInfo() {
        return "Type: " + collectionType() + "\n" +
                "Initialization date: " + (lastInitTime != null ? lastInitTime: "unknown") + "\n" +
                "Last save date:" + (lastSaveTime != null ? lastSaveTime : "unsaved") + "\n" +
                "Number of elements: " + collectionSize() + "\n";
    }


    /**
     * Koleksiyondaki tüm ticket nesnelerini Collection olarak döndürür.
     * Sıralama vb. işlemler için kullanılabilir.
     * @return Ticket nesnelerinin Collection'ı.
     */
    public synchronized Collection<Ticket> getAllTicketsCollection() {
        return new LinkedHashMap<>(collection).values(); // Kopyasını döndürmek daha güvenli olabilir
    }
}

