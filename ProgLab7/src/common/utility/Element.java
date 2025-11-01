package common.utility;

/**
 * Tüm domain modellerimizin ortak davranışını (ID’ye sahip olma, karşılaştırılabilirlik, doğrulama) belirleyen soyut sınıf.
 */
public abstract class Element implements Comparable<Element> {
    private long key;

    public abstract int getId();

    public long getKey() {
        return key;
    }

    public void setKey(long key) {
        this.key = key;
    }
}