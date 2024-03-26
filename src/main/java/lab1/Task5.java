package lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class SyncList {
    private final List<Integer> list;

    public SyncList(List<Integer> list) {
        this.list = list;
    }

    public synchronized void add() {
        int num = new Random().nextInt();
        list.add(num);
        System.out.println("Added " + num);
    }

    public synchronized void delete() {
        int index = new Random().nextInt(0, Math.max(list.size(), 1));
        list.remove(index);
        System.out.println("        Deleted [" + index + "] element");
    }

    public int size() {
        return list.size();
    }
}

class SynchronizedThread extends Thread {
    private final SyncList list;
    private final int type;

    public SynchronizedThread(SyncList list, int type) {
        this.list = list;
        this.type = type;
    }

    @Override
    public void run() {
        if (type == 0) {
            for (int i = 0; i < 10000; ++i) {
                list.add();
            }
        } else {
            for (int i = 0; i < 10000; ++i) {
                while (list.size() == 0) {
                    Thread.yield();
                }
                list.delete();
            }
        }
    }
}

public class Task5 {
    public static void main(String[] args) throws InterruptedException {
        SyncList list = new SyncList(new ArrayList<>());
        SynchronizedThread thread1 = new SynchronizedThread(list, 0);
        SynchronizedThread thread2 = new SynchronizedThread(list, 1);
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        System.out.println(list.size());
    }
}
