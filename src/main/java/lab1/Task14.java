package lab1;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class Task implements Executable {
    Runnable runnable;

    public Task(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void execute() {
        runnable.run();
    }
}

class TaskQueue {
    LinkedList<Task> tasks;

    public TaskQueue() {
        tasks = new LinkedList<>();
    }

    public TaskQueue(LinkedList<Task> tasks) {
        this.tasks = tasks;
    }

    public synchronized void add(Task task) {
        this.tasks.addLast(task);
    }

    public synchronized Task get() {
        try {
            return this.tasks.removeFirst();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}

public class Task14 {
    public static void main(String[] args) throws InterruptedException {
        TaskQueue queue = new TaskQueue();
        ReentrantLock locker = new ReentrantLock();
        Condition condition = locker.newCondition();
        Scanner sc = new Scanner(System.in);
        int writersNum = sc.nextInt();
        int readersNum = sc.nextInt();
        ThreadPoolExecutor writersPool = new ThreadPoolExecutor(writersNum, writersNum, 30L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadPoolExecutor readersPool = new ThreadPoolExecutor(readersNum, readersNum, 30L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        Thread writersPoolFill = new Thread(() -> {
            for (int i = 0; i < 100; ++i) {
                writersPool.execute(() -> {
                    locker.lock();
                    try {
                        queue.add(new Task(() -> System.out.println("       Read")));
                        System.out.println("Write");
                        condition.signal();
                    } finally {
                        locker.unlock();
                    }
                });
            }
        });
        Thread readersPoolFill = new Thread(() -> {
            for (int i = 0; i < readersPool.getMaximumPoolSize(); ++i) {
                readersPool.execute(() -> {
                    try {
                        while (true) {
                            locker.lock();
                            try {
                                while (queue.tasks.size() == 0) {
                                    if (!condition.await(100L, TimeUnit.MILLISECONDS)) {
                                        break;
                                    }
                                }
                                queue.get().execute();
                            } finally {
                                locker.unlock();
                            }
                        }
                    } catch (Exception ignored) {

                    }
                });
            }
        });
        writersPoolFill.start();
        readersPoolFill.start();
        writersPoolFill.join();
        readersPoolFill.join();

        writersPool.shutdown();
        readersPool.shutdown();
        assert queue.tasks.isEmpty();
    }
}
