package lab1;

public class Task1 {
    public static void main(String[] args) {
        Thread thread = Thread.currentThread();
        System.out.println("Thread id: " + thread.getId());
        System.out.println("Thread name: " + thread.getName());
        System.out.println("Thread priority: " + thread.getPriority());
        System.out.println("Thread is daemon: " + thread.isDaemon());
        System.out.println("Thread is interrupted: " + thread.isInterrupted());
        System.out.println("Thread is alive: " + thread.isAlive());
        System.out.println("Thread group name: " + thread.getThreadGroup().getName());
        System.out.println("Thread state: " + thread.getState());
        System.out.println("Thread contextClassLoader name: " + thread.getContextClassLoader().getName());
    }
}
