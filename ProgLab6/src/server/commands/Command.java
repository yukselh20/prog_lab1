package server.commands;

import common.dto.Request;
import common.dto.Response;
import server.managers.CollectionManager;


/**
 * Sunucu tarafındaki tüm komut sınıfları için temel abstract sınıf.
 * Bu yapı, "Command" tasarım desenini uygular.
 */

public abstract class Command {
    // Bütün komutların koleksiyonu yönetmek için ihtiyaç duyduğu ortak alan (shared state).
    protected final CollectionManager collectionManager;

    /**
     * Alt sınıfların CollectionManager'ı almasını zorunlu kılan constructor.
     * @param collectionManager koleksiyon yöneticisi.
     */
    public Command(CollectionManager collectionManager) {
        this.collectionManager = collectionManager;
    }

    public abstract Response execute(Request request);
}