package lab1;

class Thread1 extends Thread {
    public Thread1() {
        super();
    }

    public Thread1(Runnable target) {
        super(target);
    }

    public Thread1(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public Thread1(String name) {
        super(name);
    }

    public Thread1(ThreadGroup group, String name) {
        super(group, name);
    }

    public Thread1(Runnable target, String name) {
        super(target, name);
    }

    public Thread1(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public Thread1(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public Thread1(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
    }
}

class Thread2 extends Thread {
    public Thread2() {
        super();
    }

    public Thread2(Runnable target) {
        super(target);
    }

    public Thread2(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public Thread2(String name) {
        super(name);
    }

    public Thread2(ThreadGroup group, String name) {
        super(group, name);
    }

    public Thread2(Runnable target, String name) {
        super(target, name);
    }

    public Thread2(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public Thread2(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public Thread2(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
    }
}

class Thread3 extends Thread {
    public Thread3() {
        super();
    }

    public Thread3(Runnable target) {
        super(target);
    }

    public Thread3(ThreadGroup group, Runnable target) {
        super(group, target);
    }

    public Thread3(String name) {
        super(name);
    }

    public Thread3(ThreadGroup group, String name) {
        super(group, name);
    }

    public Thread3(Runnable target, String name) {
        super(target, name);
    }

    public Thread3(ThreadGroup group, Runnable target, String name) {
        super(group, target, name);
    }

    public Thread3(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public Thread3(ThreadGroup group, Runnable target, String name, long stackSize, boolean inheritThreadLocals) {
        super(group, target, name, stackSize, inheritThreadLocals);
    }
}

public class Task3 {
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println("I am not main thread");
        };
        Thread1 thread1 = new Thread1(runnable);
        Thread2 thread2 = new Thread2(runnable);
        Thread3 thread3 = new Thread3(runnable);
        thread1.start();
        thread2.start();
        thread3.start();
        thread1.join();
        thread2.join();
        thread3.join();
        System.out.println("Main end");

    }
}
