package lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class LockListManager implements Runnable {
    Condition condition;
    ReentrantLock lock;
    List<Integer> list;
    boolean type;

    public LockListManager(Condition condition, ReentrantLock lock, List<Integer> list, boolean type) {
        this.condition = condition;
        this.lock = lock;
        this.list = list;
        this.type = type;
    }

    @Override
    public void run() {
        final Random rand = new Random();
        for (int i = 0; i < 10_000; ++i) {
            lock.lock();
            if (type) {
                int num = rand.nextInt();
                list.add(num);
                System.out.println("Added " + num);
                condition.signalAll();
            } else {
                if (list.size() == 0) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
//                while (list.size() == 0) {
//                    lock.unlock();
//                    lock.lock();
//                }
                int index = rand.nextInt(0, list.size());
                list.remove(index);
                System.out.println("        Deleted [" + index + "] element");
            }
            lock.unlock();
        }
    }
}

public class Task8 {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        Thread1 thread1 = new Thread1(new LockListManager(condition, lock, list, true));
        Thread2 thread2 = new Thread2(new LockListManager(condition, lock, list, false));
        thread1.start();
        thread2.start();
        thread2.join();
        thread1.join();
        System.out.println(list);
    }
}
