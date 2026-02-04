package mcts;
import java.util.LinkedHashMap;
import java.util.Map;
import Game.*;

public class CacheMcts<K, V> extends LinkedHashMap<K, V> {
    private final int maxEntries;

    public CacheMcts(int maxEntries) {
        super(maxEntries + 1, 1.0f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxEntries;
    }
}
