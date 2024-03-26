package lab1;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class LockPingPongWriter implements Runnable {
    Condition condition;
    ReentrantLock lock;
    final String text;

    LockPingPongWriter(Condition condition, ReentrantLock lock, String text) {
        this.condition = condition;
        this.lock = lock;
        this.text = text;
    }

    @Override
    public void run() {
        while (true) {
            lock.lock();
            System.out.println(text);
            condition.signal();
            try {
                condition.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            lock.unlock();
        }
    }
}

public class Task9 {
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        LockPingPongWriter pingWriter = new LockPingPongWriter(condition, lock, "Ping");
        LockPingPongWriter pongWriter = new LockPingPongWriter(condition, lock, "       Pong");
        Thread threadPing = new Thread(pingWriter);
        Thread threadPong = new Thread(pongWriter);
        threadPing.start();
        threadPong.start();
    }
}
