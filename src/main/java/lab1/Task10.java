package lab1;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyConcurrentHashMap<K, V> {
    private final int slotNumber;
    HashMap<K, V>[] maps;
    ReentrantReadWriteLock[] lockers;

    public MyConcurrentHashMap(int slotNumber) {
        this.slotNumber = slotNumber;
        this.maps = new HashMap[slotNumber];
        this.lockers = new ReentrantReadWriteLock[slotNumber];
        for (int i = 0; i < slotNumber; ++i) {
            this.maps[i] = new HashMap<>();
            this.lockers[i] = new ReentrantReadWriteLock();
        }
    }

    public int size() {
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            int size = 0;
            for (var map : maps) {
                size += map.size();
            }
            return size;
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }

    public boolean isEmpty() {
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            return maps == null || this.slotNumber == 0 || this.size() == 0;
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }

    public boolean containsKey(K key) {
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            for (var map : maps) {
                if (map.containsKey(key)) {
                    return true;
                }
            }
            return false;
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }

    public boolean containsValue(V value) {
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            for (var map : maps) {
                if (map.containsValue(value)) {
                    return true;
                }
            }
            return false;
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }

    public V get(K key) {
        int currentSlot = key.hashCode() % slotNumber;
        lockers[currentSlot].readLock().lock();
        HashMap<K, V> currentMap = maps[currentSlot];
        try {
            return currentMap.get(key);
        } finally {
            lockers[currentSlot].readLock().unlock();
        }
    }

    public V put(K key, V value) {
        int currentSlot = key.hashCode() % slotNumber;
        lockers[currentSlot].writeLock().lock();
        HashMap<K, V> currentMap = maps[currentSlot];
        try {
            return currentMap.put(key, value);
        } finally {
            lockers[currentSlot].writeLock().unlock();
        }
    }

    public V remove(K key) {
        int currentSlot = key.hashCode() % slotNumber;
        HashMap<K, V> currentMap = maps[currentSlot];
        lockers[currentSlot].writeLock().lock();
        try {
            return currentMap.remove(key);
        } finally {
            lockers[currentSlot].writeLock().unlock();
        }
    }

    public void clear() {
        Arrays.stream(lockers).forEach(locker -> locker.writeLock().lock());
        for (var map : maps) {
            map.clear();
        }
        Arrays.stream(lockers).forEach(locker -> locker.writeLock().unlock());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyConcurrentHashMap<?, ?> that = (MyConcurrentHashMap<?, ?>) o;
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            return slotNumber == that.slotNumber && Arrays.equals(maps, that.maps);
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }

    @Override
    public int hashCode() {
        Arrays.stream(lockers).forEach(locker -> locker.readLock().lock());
        try {
            int result = Objects.hash(slotNumber);
            result = 31 * result + Arrays.hashCode(maps);
            return result;
        } finally {
            Arrays.stream(lockers).forEach(locker -> locker.readLock().unlock());
        }
    }
}

class Adder implements Runnable {
    MyConcurrentHashMap<Integer, String> map;
    Integer key;
    String value;

    public Adder(MyConcurrentHashMap<Integer, String> map, Integer key, String value) {
        this.map = map;
        this.key = key;
        this.value = value;
    }

    @Override
    public void run() {
        System.out.println(map.put(key, value));
    }
}

class Deleter implements Runnable {
    MyConcurrentHashMap<Integer, String> map;
    Integer key;

    public Deleter(MyConcurrentHashMap<Integer, String> map, Integer key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void run() {
        System.out.println(map.remove(key));
    }
}

class Getter implements Runnable {
    MyConcurrentHashMap<Integer, String> map;
    Integer key;

    public Getter(MyConcurrentHashMap<Integer, String> map, Integer key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void run() {
        System.out.println(map.get(key));
    }
}

public class Task10 {
    public static void main(String[] args) throws InterruptedException {
        MyConcurrentHashMap<Integer, String> map = new MyConcurrentHashMap<>(4);
        //Adders
        Thread thread1 = new Thread(new Adder(map, 1, "A"));
        Thread thread2 = new Thread(new Adder(map, 1, "B"));
        Thread thread3 = new Thread(new Adder(map, 10, "C"));
        Thread thread4 = new Thread(new Adder(map, 2, "D"));
        Thread thread5 = new Thread(new Adder(map, 3, "E"));
        Thread thread6 = new Thread(new Adder(map, 4, "F"));
        //Deleters
        Thread thread7 = new Thread(new Deleter(map, 4));
        Thread thread8 = new Thread(new Deleter(map, 3));
        Thread thread9 = new Thread(new Deleter(map, 4));
        //Getters
        Thread thread10 = new Thread(new Getter(map, 4));
        Thread thread11 = new Thread(new Getter(map, 1));
        Thread thread12 = new Thread(new Getter(map, 2));
        //Starting
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread1.join();
        thread2.join();
        thread3.join();
        thread4.join();
        thread5.join();
        thread6.join();
        System.out.println(Arrays.toString(map.maps));
        thread7.start();
        thread8.start();
        thread9.start();
        thread7.join();
        thread8.join();
        thread9.join();
        System.out.println(Arrays.toString(map.maps));
        thread10.start();
        thread11.start();
        thread12.start();
        thread10.join();
        thread11.join();
        thread12.join();
        System.out.println(Arrays.toString(map.maps));
    }
}
