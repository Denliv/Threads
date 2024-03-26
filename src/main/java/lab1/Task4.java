package lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Task4 {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = new ArrayList<>();
        final Random rand = new Random();
        Runnable runnable1 = () -> {
            for (int i = 0; i < 10000; ++i) {
                int num = rand.nextInt();
                synchronized (list) {
                    list.add(num);
                }
                System.out.println("Added " + num);
            }
        };
        Runnable runnable2 = () -> {
            for (int i = 0; i < 10000; ++i) {
                while (list.size() == 0) {
                    Thread.yield();
                }
                int index = rand.nextInt(0, list.size());
                synchronized (list) {
                    list.remove(index);
                }
                System.out.println("Deleted [" + index + "] element");
            }
        };
        Thread1 thread1 = new Thread1(runnable1);
        Thread2 thread2 = new Thread2(runnable2);
        thread1.start();
        thread2.start();
        thread2.join();
        thread1.join();
        System.out.println(list);
    }
}
