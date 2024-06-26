package lab1;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class TaskList implements Executable {
    LinkedList<Task> tasks;

    public TaskList(LinkedList<Task> tasks) {
        this.tasks = new LinkedList<>();
        for (int i = 1; i <= tasks.size(); ++i) {
            Task currentTask = tasks.getFirst();
            currentTask.number = i;
            currentTask.stageNumber = tasks.size();
            this.tasks.addLast(currentTask);
        }
    }

    public TaskList(Task task, int count) {
        this.tasks = new LinkedList<>();
        for (int i = 1; i <= count; ++i) {
            Task currentTask = new Task(task.runnable);
            currentTask.number = i;
            currentTask.stageNumber = count;
            this.tasks.addLast(currentTask);
        }
    }

    @Override
    public void execute() {
        tasks.removeFirst().execute();
    }
}

class TaskListQueue {
    LinkedList<TaskList> taskLists;

    public TaskListQueue() {
        taskLists = new LinkedList<>();
    }

    public synchronized void add(TaskList taskList) {
        this.taskLists.addLast(taskList);
    }

    public synchronized Task get() {
        try {
            if (this.taskLists.getFirst().tasks.size() == 1) {
                return this.taskLists.removeFirst().tasks.removeFirst();
            }
            return this.taskLists.getFirst().tasks.removeFirst();
        } catch (NoSuchElementException ex) {
            return null;
        }
    }
}

class WatcherInfo {
    String name;
    int number;
    int stageNumber;

    public WatcherInfo(String name, int number, int stageNumber) {
        this.name = name;
        this.number = number;
        this.stageNumber = stageNumber;
    }
}

public class Task15 {
    public static void main(String[] args) throws InterruptedException {
        TaskListQueue queue = new TaskListQueue();
        List<WatcherInfo> watcherList = new ArrayList<>();
        ReentrantLock locker = new ReentrantLock();
        Condition condition = locker.newCondition();
        ReentrantLock watcherLocker = new ReentrantLock();
        Scanner sc = new Scanner(System.in);
        int writersNum = sc.nextInt();
        int readersNum = sc.nextInt();
        ThreadPoolExecutor writersPool = new ThreadPoolExecutor(writersNum, Integer.MAX_VALUE, 30L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        ThreadPoolExecutor readersPool = new ThreadPoolExecutor(readersNum, Integer.MAX_VALUE, 30L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

        Thread watcher = new Thread(
                () -> {
                    for (/*int i = 0; i < 100; ++i*/;;) {
                        try {
                            Thread.sleep(2);
                            List<WatcherInfo> tempList;
                            watcherLocker.lock();
                            try {
                                tempList = new ArrayList<>(watcherList);
                            } finally {
                                watcherLocker.unlock();
                            }
                            StringBuilder tempString = new StringBuilder("WATCHER_INFO:\n");
                            if (tempList.isEmpty()) {
                                tempString.append("EMPTY");
                            }
                            else {
                                tempList.forEach(info -> tempString.append(info.name).append("info: ").append(info.number).append("/").append(info.stageNumber).append("\n"));
                            }
                            System.out.println(tempString);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
        );
        Thread writersPoolFill = new Thread(() -> {
            for (/*int i = 0; i < 100; ++i*/;;) {
                writersPool.execute(() -> {
                    locker.lock();
                    try {
                        queue.add(
                                /*new TaskList(
                                        new LinkedList<>(
                                                List.of(new Task(() -> System.out.println("       Read"))))
                                )*/
                                new TaskList(new Task(() -> System.out.println("       Read")), (int)(Math.random() * 5 + 1))
                        );
                        System.out.println("Write");
                        condition.signal();
                    } finally {
                        locker.unlock();
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
        Thread readersPoolFill = new Thread(() -> {
            for (int i = 0; i < readersPool.getMaximumPoolSize(); ++i) {
                readersPool.execute(() -> {
                    try {
                        while (true) {
                            Task currentTask;
                            locker.lock();
                            try {
                                while (queue.taskLists.size() == 0) {
                                    if (!condition.await(100L, TimeUnit.MILLISECONDS)) {
                                        return;
                                    }
                                }
                                currentTask = queue.get();
                            } finally {
                                locker.unlock();
                            }
                            WatcherInfo info = new WatcherInfo(currentTask.toString(), currentTask.number, currentTask.stageNumber);
                            watcherLocker.lock();
                            try {
                                watcherList.add(info);
                            } finally {
                                watcherLocker.unlock();
                            }
                            currentTask.execute();
                            Thread.sleep(1);
                            watcherLocker.lock();
                            try {
                                watcherList.remove(info);
                            } finally {
                                watcherLocker.unlock();
                            }
                        }
                    } catch (Exception ignored) {

                    }
                });
            }
        });
        watcher.start();
        writersPoolFill.start();
        readersPoolFill.start();
        writersPoolFill.join();
        readersPoolFill.join();
        watcher.join();
        writersPool.shutdown();
        readersPool.shutdown();
        assert queue.taskLists.isEmpty();
    }
}
