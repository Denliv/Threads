package lab1;

public class Task2 {
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable1 = () -> {
            System.out.println("Thread_1:Hello");
            Runnable runnable2 = () -> {
                System.out.println("Thread_2:Hello");
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Thread_2:I am done!");
            };
            Thread thread2 = new Thread(runnable2);
            thread2.start();
            try {
                thread2.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Thread_1:I am done!");
        };
        Thread thread1 = new Thread(runnable1);
        thread1.start();
        thread1.join();
        System.out.println("Main end");
    }
}
