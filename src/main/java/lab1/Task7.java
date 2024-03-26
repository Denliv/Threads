package lab1;

class Monitor {
    private boolean flag;

    public Monitor(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}

class Writer implements Runnable {
    private final Monitor monitor;
    private final String text;

    public Writer(Monitor monitor, String text) {
        this.monitor = monitor;
        this.text = text;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (monitor) {
                if (monitor.isFlag()) {
                    monitor.setFlag(false);
                    try {
                        monitor.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(text);
                monitor.setFlag(true);
                monitor.notify();
            }
        }
    }
}

public class Task7 {
    public static void main(String[] args) {
        Monitor monitor = new Monitor(false);
        Writer pingWriter = new Writer(monitor, "Ping");
        Writer pongWriter = new Writer(monitor, "       Pong");
        Thread threadPing = new Thread(pingWriter);
        Thread threadPong = new Thread(pongWriter);
        threadPing.start();
        threadPong.start();
    }
}
