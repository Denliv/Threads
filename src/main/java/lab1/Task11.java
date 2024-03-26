package lab1;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class Formatter {
    private final ThreadLocal<SimpleDateFormat> dateFormat;

    public Formatter(ThreadLocal<SimpleDateFormat> dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String format(Date date) {
        return dateFormat.get().format(date);
    }
}

class DateFormatter implements Runnable {
    ThreadLocal<SimpleDateFormat> dateFormat;
    Date date;
    Formatter formatter;
    String text;

    public DateFormatter(ThreadLocal<SimpleDateFormat> dateFormat, Date date, Formatter formatter, String text) {
        this.dateFormat = dateFormat;
        this.date = date;
        this.formatter = formatter;
        this.text = text;
    }

    @Override
    public void run() {
        dateFormat.set(new SimpleDateFormat("dd/MM/yy - HH:mm:ss"));
        System.out.println(text + ": " + formatter.format(date));
    }
}

public class Task11 {
    public static void main(String[] args) {
        ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<>();
        Formatter formatter = new Formatter(dateFormat);
        Date date1 = new Date(104, Calendar.MARCH, 2);
        Date date2 = new Date(105, Calendar.APRIL, 3);
        Date date3 = new Date(106, Calendar.MAY, 4);
        Date date4 = new Date(107, Calendar.JUNE, 5);
        Date date5 = new Date(108, Calendar.JULY, 6);
        Thread thread1 = new Thread(new DateFormatter(dateFormat, date1, formatter, "first"));
        Thread thread2 = new Thread(new DateFormatter(dateFormat, date2, formatter, "second"));
        Thread thread3 = new Thread(new DateFormatter(dateFormat, date3, formatter, "third"));
        Thread thread4 = new Thread(new DateFormatter(dateFormat, date4, formatter, "forth"));
        Thread thread5 = new Thread(new DateFormatter(dateFormat, date5, formatter, "fifth"));
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
    }
}
