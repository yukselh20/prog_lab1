package common.network;

/**
 * Bu sınıf, istemci ve sunucu arasında paylaşılan ağ yapılandırma sabitlerini içerir.
 * "Magic numbers" (kod içine gömülü sihirli sayılar) kullanımını engeller.
 */
public class NetworkConfig {
    public static final int MAX_PACKET_SIZE = 8192;
}