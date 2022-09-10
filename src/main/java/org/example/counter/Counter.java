package org.example.counter;

public class Counter implements Runnable {

    private int count = 0;

    public void increment(){
        count++;
    }

    public void decremente(){
        count--;
    }

    public int getValue(){
        return count;
    }

    @Override
    public void run() {
        //자원 공유 문제는 동기화 처리를 하면 해결이 된다.
        synchronized (this) {
            this.increment();
            System.out.println("value for Thread After increment " + Thread.currentThread().getName() + " " + this.getValue()); // 1
            this.decremente();
            System.out.println("value for Thread After increment " + Thread.currentThread().getName() + " " + this.getValue()); // 0
        }
    }
}
