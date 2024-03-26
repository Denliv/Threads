package lab1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Task6 {
    public static void main(String[] args) throws InterruptedException {
        List<Integer> list = Collections.synchronizedList(new ArrayList<>());
        Runnable runnable1 = () -> {
            for (int i = 0; i < 10000; ++i) {
                int num = new Random().nextInt();
                list.add(num);
                System.out.println("Added " + num);
            }
        };
        Runnable runnable2 = () -> {
            for (int i = 0; i < 10000; ++i) {
                while (list.size() == 0) {
                    Thread.yield();
                }
                int index = new Random().nextInt(0, Math.max(list.size(), 1));
                list.remove(index);
                System.out.println("        Deleted [" + index + "] element");
            }
        };
        Thread1 thread1 = new Thread1(runnable1);
        Thread1 thread2 = new Thread1(runnable2);
        thread1.start();
        thread2.start();
        thread2.join();
        thread1.join();
        System.out.println(list);
    }
}
