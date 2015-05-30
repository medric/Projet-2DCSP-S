package models;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Epulapp on 30/05/2015.
 */
public class CustomHashMap<K,V> extends HashMap<K,V> {

    public  CustomHashMap() {
        super();
    }

    public K indexOf(int index) {
        K key = null;
        int i = 1;
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
