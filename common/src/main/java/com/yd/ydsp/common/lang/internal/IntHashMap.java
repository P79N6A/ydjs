package com.yd.ydsp.common.lang.internal;

/**
 * Created by zengyixun on 17/3/18.
 */

import com.yd.ydsp.common.lang.ArrayUtil;

public class IntHashMap {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;
    private static final int MAXIMUM_CAPACITY = 1073741824;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private IntHashMap.Entry[] table;
    private int count;
    private int threshold;
    private float loadFactor;

    public IntHashMap() {
        this(16, 0.75F);
    }

    public IntHashMap(int initialCapacity) {
        this(initialCapacity, 0.75F);
    }

    public IntHashMap(int initialCapacity, float loadFactor) {
        if(initialCapacity < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
        } else {
            if(initialCapacity > 1073741824) {
                initialCapacity = 1073741824;
            }

            if(loadFactor <= 0.0F) {
                throw new IllegalArgumentException("Illegal Load: " + loadFactor);
            } else {
                int capacity;
                for(capacity = 1; capacity < initialCapacity; capacity <<= 1) {
                    ;
                }

                this.loadFactor = loadFactor;
                this.table = new IntHashMap.Entry[capacity];
                this.threshold = (int)((float)capacity * loadFactor);
            }
        }
    }

    public int size() {
        return this.count;
    }

    public boolean isEmpty() {
        return this.count == 0;
    }

    public boolean containsKey(int key) {
        IntHashMap.Entry[] tab = this.table;
        int hash = key;
        int index = (key & 2147483647) % tab.length;

        for(IntHashMap.Entry e = tab[index]; e != null; e = e.next) {
            if(e.hash == hash) {
                return true;
            }
        }

        return false;
    }

    public boolean containsValue(Object value) {
        IntHashMap.Entry[] tab = this.table;
        boolean valueIsNull = value == null;
        int i = tab.length;

        while(i-- > 0) {
            for(IntHashMap.Entry e = tab[i]; e != null; e = e.next) {
                if(valueIsNull) {
                    if(e.value == null) {
                        return true;
                    }
                } else if(value.equals(e.value)) {
                    return true;
                }
            }
        }

        return false;
    }

    public Object get(int key) {
        IntHashMap.Entry[] tab = this.table;
        int hash = key;
        int index = (key & 2147483647) % tab.length;

        for(IntHashMap.Entry e = tab[index]; e != null; e = e.next) {
            if(e.hash == hash) {
                return e.value;
            }
        }

        return null;
    }

    public Object put(int key, Object value) {
        IntHashMap.Entry[] tab = this.table;
        int hash = key;
        int index = (key & 2147483647) % tab.length;

        IntHashMap.Entry e;
        for(e = tab[index]; e != null; e = e.next) {
            if(e.hash == hash) {
                Object old = e.value;
                e.value = value;
                return old;
            }
        }

        if(this.count >= this.threshold) {
            this.rehash();
            tab = this.table;
            index = (hash & 2147483647) % tab.length;
        }

        e = new IntHashMap.Entry(hash, key, value, tab[index]);
        tab[index] = e;
        ++this.count;
        return null;
    }

    public Object remove(int key) {
        IntHashMap.Entry[] tab = this.table;
        int hash = key;
        int index = (key & 2147483647) % tab.length;
        IntHashMap.Entry e = tab[index];

        for(IntHashMap.Entry prev = null; e != null; e = e.next) {
            if(e.hash == hash) {
                if(prev != null) {
                    prev.next = e.next;
                } else {
                    tab[index] = e.next;
                }

                --this.count;
                Object oldValue = e.value;
                e.value = null;
                return oldValue;
            }

            prev = e;
        }

        return null;
    }

    public void clear() {
        IntHashMap.Entry[] tab = this.table;
        int index = tab.length;

        while(true) {
            --index;
            if(index < 0) {
                this.count = 0;
                return;
            }

            tab[index] = null;
        }
    }

    public int[] keys() {
        if(this.count == 0) {
            return ArrayUtil.EMPTY_INT_ARRAY;
        } else {
            int[] keys = new int[this.count];
            int index = 0;

            for(int i = 0; i < this.table.length; ++i) {
                for(IntHashMap.Entry entry = this.table[i]; entry != null; entry = entry.next) {
                    keys[index++] = entry.key;
                }
            }

            return keys;
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append('{');
        int[] keys = this.keys();

        for(int i = 0; i < keys.length; ++i) {
            int key = keys[i];
            Object value = this.get(key);
            if(i > 0) {
                buffer.append(", ");
            }

            buffer.append(key).append('=').append(value == this?"(this Map)":value);
        }

        buffer.append('}');
        return buffer.toString();
    }

    protected void rehash() {
        int oldCapacity = this.table.length;
        IntHashMap.Entry[] oldMap = this.table;
        int newCapacity = oldCapacity * 2;
        IntHashMap.Entry[] newMap = new IntHashMap.Entry[newCapacity];
        this.threshold = (int)((float)newCapacity * this.loadFactor);
        this.table = newMap;
        int i = oldCapacity;

        IntHashMap.Entry e;
        int index;
        while(i-- > 0) {
            for(IntHashMap.Entry old = oldMap[i]; old != null; newMap[index] = e) {
                e = old;
                old = old.next;
                index = (e.hash & 2147483647) % newCapacity;
                e.next = newMap[index];
            }
        }

    }

    protected int getCapacity() {
        return this.table.length;
    }

    protected int getThreshold() {
        return this.threshold;
    }

    protected static class Entry {
        protected int hash;
        protected int key;
        protected Object value;
        protected IntHashMap.Entry next;

        protected Entry(int hash, int key, Object value, IntHashMap.Entry next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }
}
