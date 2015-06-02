package models;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Custom HashMap class.
 * add methods to HashMap.
 *
 * @author Medric, Gabi, P-J
 * @version 1.0
 */
public class CustomHashMap<K,V> extends HashMap<K,V> {

    public  CustomHashMap() {
        super();
    }

    /**
     * Return key object according to his index
     * @param index the index of key
     * @return Key object
     */
    public K indexOf(int index) {
        K key = null;
        int i = 0;
        K value = null;
        Iterator<K> iterator = this.keySet().iterator();

        while (iterator.hasNext()) {
            value = iterator.next();
            if(i == index) {
                key = value;
            }
            i++;
        }

        return key;
    }
}
