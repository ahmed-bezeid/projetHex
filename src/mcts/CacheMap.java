package Mcts;
import Game.*;

import java.util.LinkedHashMap;
import java.util.Map;
/*CacheMap est une extension de LinkedHashMap configurée en mode Access Order. Elle sert de mémoire à court terme pour l'IA*/
public class CacheMap<K, V> extends LinkedHashMap<K, V> {
    private final int maxEntries;//la capacité initiale

    public CacheMap(int maxEntries) {
        super(maxEntries + 1, 1.0f, true);// 1.0f (Load Factor),true (Access Order)
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}

