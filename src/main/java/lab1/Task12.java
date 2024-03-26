package lab1;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

class Message {
    String emailAddress;
    String sender;
    String subject;
    String body;

    public Message(String emailAddress, String sender, String subject, String body) {
        this.emailAddress = emailAddress;
        this.sender = sender;
        this.subject = subject;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(emailAddress, message.emailAddress) && Objects.equals(sender, message.sender) && Objects.equals(subject, message.subject) && Objects.equals(body, message.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailAddress, sender, subject, body);
    }

    @Override
    public String toString() {
        return "*****************************\n" +
                "Sender: " + sender + '\n' +
                "Subject: " + subject + '\n' +
                body + '\n' +
                "*****************************\n";
    }
}
class Transport {
    private final Pointer[] pointers;
    public Transport(Pointer[] pointers) {
        this.pointers = pointers;
    }

    public void send(Message message) {
        Runnable runnable = () -> {
            String email = message.emailAddress;
            String text = message.toString();
            int pointerNum = Math.abs(email.hashCode()) % pointers.length;
            pointers[pointerNum].locker.lock();
            int currentPointer = pointers[pointerNum].point;
            pointers[pointerNum].point = pointers[pointerNum].point + text.length();
            pointers[pointerNum].locker.unlock();
            File file = new File("C:\\Users\\Даниил\\Desktop\\Threads\\src\\main\\java\\lab1\\" + email + ".txt");
            try {
                if (!file.exists())
                    file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try (RandomAccessFile fw = new RandomAccessFile(file, "rw")) {
                fw.seek(currentPointer);
                fw.write(text.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        runnable.run();
    }
}
class ThreadMessage extends Thread {
    Transport transport;
    Message[] messages;

    public ThreadMessage(Pointer[] pointers, Message[] messages) {
        this.transport = new Transport(pointers);
        this.messages = messages;
    }

    @Override
    public synchronized void run() {
        for (Message message : messages) {
            transport.send(message);
        }
    }
}
class Pointer{
    int point;
    ReentrantLock locker;

    public Pointer(int point) {
        this.point = point;
        locker = new ReentrantLock();
    }
}
public class Task12 {
    public static void main(String[] args) throws IOException {
        List<String> emailList = new ArrayList<>();
        try (RandomAccessFile fr = new RandomAccessFile("C:\\Users\\Даниил\\Desktop\\Threads\\src\\main\\java\\lab1\\email data.txt", "r")){
            while (fr.getFilePointer() < fr.length()) {
                emailList.add(fr.readLine());
            }
        }
        Pointer[] pointers = new Pointer[emailList.size()];
        for (int i = 0; i < pointers.length; ++i) {
            pointers[i] = new Pointer(0);
        }
        System.out.println(emailList);
        Message message1_1 = new Message(emailList.get(0), "A_1", "A_1", "A_1");
        Message message1_2 = new Message(emailList.get(0), "A_2", "A_2", "A_2");
        Message message1_3 = new Message(emailList.get(0), "A_3", "A_3", "A_3");
        Message message2_1 = new Message(emailList.get(0), "B_1", "B_1", "B_1");
        Message message2_2 = new Message(emailList.get(0), "B_2", "B_2", "B_2");
        Message message2_3 = new Message(emailList.get(0), "B_3", "B_3", "B_3");
        Message message3_1 = new Message(emailList.get(0), "C_1", "C_1", "C_1");
        Message message3_2 = new Message(emailList.get(0), "C_2", "C_2", "C_2");
        Message message3_3 = new Message(emailList.get(0), "C_3", "C_3", "C_3");
        Message message4_1 = new Message(emailList.get(0), "D_1", "D_1", "D_1");
        Message message4_2 = new Message(emailList.get(0), "D_2", "D_2", "D_2");
        Message message4_3 = new Message(emailList.get(0), "D_3", "D_3", "D_3");

        Thread thread1 = new ThreadMessage(pointers, new Message[]{message1_1, message1_2, message1_3});
        Thread thread2 = new ThreadMessage(pointers, new Message[]{message2_1, message2_2, message2_3});
        Thread thread3 = new ThreadMessage(pointers, new Message[]{message3_1, message3_2, message3_3});
        Thread thread4 = new ThreadMessage(pointers, new Message[]{message4_1, message4_2, message4_3});
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }
}
